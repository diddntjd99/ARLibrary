const express = require('express');
const app = express();
var socketio = require('socket.io');
var fs = require('fs');
var server = app.listen(2163, () => {
    console.log('Example app listening on port 2163!');
});

var io = socketio.listen(server);

var mongoose = require('mongoose');

mongoose.connect('mongodb://localhost:27017/ARLibrary');

var db = mongoose.connection;

db.on('error', function () {
    console.log('Connection Failed!');
});

db.once('open', function () {
    console.log('Connected!');
});

// 6. Schema 생성. (혹시 스키마에 대한 개념이 없다면, 입력될 데이터의 타입이 정의된 DB 설계도 라고 생각하면 됩니다.)
var book = mongoose.Schema({
    title: String, //책명
    publisher: String, //출판사
    author: String, //저자
    genre: String, //장르
    goal_point: String, //책 위치(유니티 표현)
    direction: String, //왼쪽, 오른쪽 방향
    tag: { //태그 이미지
        data: Buffer,
        contentType: String
    },
    book_image: { //책 이미지
        data: Buffer,
        contentType: String
    },
    rental: Boolean, //도서 대출 상태 Y/N로 표현
    publication_year: Number, //출판연도
    book_location: Number, //소장위치(층)
    introduction: String, //책 소개
    ISBN: String,
    registration_Number: String, //등록번호
    call_Number: String //청구기호
});
var bookReview = mongoose.Schema({
    user_id: String, //유저 id
    title: String, //책명
    review: String, //리뷰
    rating: Number //평점
});
var user = mongoose.Schema({
    user_id: String, //유저 id
    passwd: String, //유저 비밀번호
    name: String //이름
});
var rentalHistory = mongoose.Schema({
    user_id: String, //유저 id
    title: String, //책 이름
    registration_Number: String, //책 등록번호
    rental_date: String, //대출 날짜, 2021/01/01 식으로 기입
    return_date: String //반납 날짜
});
var reservation = mongoose.Schema({
    user_id: String, //유저 id
    title: String, //책 이름
    registration_Number: String, //책 등록번호
    reservation_date: String //예약일
});

// 7. 정의된 스키마를 객체처럼 사용할 수 있도록 model() 함수로 컴파일
var Book = mongoose.model('Book', book);
var BookReview = mongoose.model('BookReview', bookReview);
var User = mongoose.model('User', user);
var RentalHistory = mongoose.model('RentalHistory', rentalHistory);
var Reservation = mongoose.model('Reservation', reservation);

io.on('connection', function (socket) {
    console.log("Login New Client");

    socket.on('book_find', function (data) { //책 검색
        Book.find({
            title: {
                $regex: data,
                $options: 'i'
            }
        }, function (err, r) {
            //data로 전송된 문자열이 포함되어있는 결과물
            //regex-특정 문자열 포함 속성, options-대소문자 구분x 속성
            if (err) {
                console.log("error");
            }

            r.forEach(function (i) {
                var b = new Buffer(i.book_image.data + i.book_image.contentType, 'base64');

                fs.writeFile("images/" + i.title + ".jpg", b, function (err) {
                    if (err)
                        console.log(err);
                });
            });

            console.log(r);
            socket.emit("book_find_return", r);
        });
    });

    socket.on('book_review', function (data) { //리뷰 보기
        BookReview.find({
            title: {
                $regex: data,
                $options: 'i'
            }
        }, function (err, r) {
            //data로 전송된 문자열이 포함되어있는 결과물
            //regex-특정 문자열 포함 속성, options-대소문자 구분x 속성
            if (err) {
                console.log("error");
            }
            console.log(r);
            socket.emit("book_review_return", r);
        });
    });

    socket.on('book_review_add', function (data) { //리뷰 추가
        console.log(data);

        RentalHistory.findOne({
            user_id: {
                $regex: data.user_id,
                $options: 'i'
            },
            title: {
                $regex: data.title,
                $options: 'i'
            }
        }, function (err, r) {
            if (r == null) { //책을 대여한 적이 없을 때
                socket.emit("reviewSave", "NoRent");
            } else {
                BookReview.findOne({
                    user_id: {
                        $regex: data.user_id,
                        $options: 'i'
                    },
                    title: {
                        $regex: data.title,
                        $options: 'i'
                    }
                }, function (err, r2) {
                    if (r2 == null) { //해당 사용자가 리뷰 등록을 한 적이 없을 때
                        var b1 = new BookReview({
                            user_id: data.user_id,
                            title: data.title,
                            review: data.review,
                            rating: data.rating
                        });
                        b1.save(function (error, data) {
                            if (error) {
                                console.log(error);
                            } else {
                                console.log('Saved!')
                            }
                        });
                        socket.emit("reviewSave", "Save");
                    } else {
                        socket.emit("reviewSave", "Exist");
                    }
                });
            }
        });
    });

    socket.on('login', function (data) { //로그인 요청
        console.log(data);

        User.findOne({
            user_id: data.user_id
        }, function (err, r) {
            //data로 전송된 문자열이 포함되어있는 결과물
            //regex-특정 문자열 포함 속성, options-대소문자 구분x 속성
            if (err) {
                console.log("error");
            }

            console.log(r);

            if (r.passwd == data.passwd) {
                socket.emit("success", r);
            } else {
                socket.emit("fail", "fail");
            }
        });
    });

    socket.on('user_rental_history', function (data) { //사용자 대출 리스트
        RentalHistory.find({
            user_id: {
                $regex: data,
                $options: 'i'
            }
        }, function (err, r) {
            //data로 전송된 문자열이 포함되어있는 결과물
            //regex-특정 문자열 포함 속성, options-대소문자 구분x 속성
            if (err) {
                console.log("error");
            }
            console.log(r);
            socket.emit("user_rental_history_return", r);
        });
    });

    socket.on('update_return_date', function (data) { //대출 연장
        RentalHistory.update({
            user_id: data.user_id,
            title: data.title
        }, {
            $set: {
                return_date: data.return_date
            }
        }, function (err, res) {
            if (err)
                throw err;
            console.log(res);
            console.log(data.user_id + " / " + data.title + " 대출 연장 완료");
        });
    });

    socket.on('delete_review', function (data) { //리뷰 삭제
        console.log(data);
        BookReview.deleteOne({
            user_id: {
                $regex: data.user_id,
                $options: 'i'
            },
            title: {
                $regex: data.title,
                $options: 'i'
            }
        }, function (err, r) {
            if (err) {
                console.log("error");
            }
            console.log("delete!");
            socket.emit("delete_review_return", "delete");
        });
    });

    socket.on('reservation', function (data) { //예약 기록 확인
        Reservation.find({
            user_id: {
                $regex: data,
                $options: 'i'
            }
        }, function (err, r) {
            //data로 전송된 문자열이 포함되어있는 결과물
            //regex-특정 문자열 포함 속성, options-대소문자 구분x 속성
            if (err) {
                console.log("error");
            }
            console.log(r);
            socket.emit("reservation_return", r);
        });
    });

    socket.on('reservation_count', function (data) { //해당 책의 예약 기록 수
        Reservation.find({
            title: {
                $regex: data,
                $options: 'i'
            }
        }, function (err, r) {
            //data로 전송된 문자열이 포함되어있는 결과물
            //regex-특정 문자열 포함 속성, options-대소문자 구분x 속성
            if (err) {
                console.log("error");
            }
            console.log("예약자 : " + r.length.toString() + "명");
            socket.emit("reservation_count_return", r.length.toString());
        });
    });

    socket.on('reservation_add', function (data) { //예약 추가
        Reservation.findOne({
            user_id: {
                $regex: data.user_id,
                $options: 'i'
            },
            title: {
                $regex: data.title,
                $options: 'i'
            }
        }, function (err, r) {
            if (r == null) { //해당 사용자가 예약 등록을 한 적이 없을 때
                var d1 = new Reservation({
                    user_id: data.user_id,
                    title: data.title,
                    registration_Number: data.registration_Number,
                    reservation_date: data.reservation_date
                });
                d1.save(function (error, data) {
                    if (error) {
                        console.log(error);
                    } else {
                        console.log('Saved!')
                    }
                });
                socket.emit("reservationSave", "Save");
            } else {
                socket.emit("reservationSave", "Exist");
            }
        });
    });

    socket.on('delete_reservation', function (data) { //예약 삭제
        console.log(data);
        Reservation.deleteOne({
            user_id: {
                $regex: data.user_id,
                $options: 'i'
            },
            title: {
                $regex: data.title,
                $options: 'i'
            }
        }, function (err, r) {
            if (err) {
                console.log("error");
            }
            console.log("delete!");
            socket.emit("delete_reservation_return", "delete");
        });
    });
});

app.get('/img/:title', function (req, res) {
    fs.readFile(__dirname + "/images/" + req.params.title + ".jpg", function (error, data) {
        res.writeHead(200, {
            'Context-Type': 'image/jpg; charset=UTF-8'
        });
        res.end(data);
    });
});
