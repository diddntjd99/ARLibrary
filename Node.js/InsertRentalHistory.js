// 1. mongoose 모듈 가져오기
var mongoose = require('mongoose');

//fs 모듈 가져오기
var fs = require('fs');

// 2. testDB 세팅
mongoose.connect('mongodb://localhost:27017/ARLibrary');

// 3. 연결된 testDB 사용
var db = mongoose.connection;

// 4. 연결 실패
db.on('error', function () {
    console.log('Connection Failed!');
});

// 5. 연결 성공
db.once('open', function () {
    console.log('Connected!');
});

// 6. Schema 생성. (혹시 스키마에 대한 개념이 없다면, 입력될 데이터의 타입이 정의된 DB 설계도 라고 생각하면 됩니다.)
var rentalHistory = mongoose.Schema({
    user_id: String, //유저 id
    title: String, //책 이름
    registration_Number: String, //책 등록번호
    rental_date: String, //대출 날짜, 2021/01/01 식으로 기입
    return_date: String //반납 날짜
});

// 7. 정의된 스키마를 객체처럼 사용할 수 있도록 model() 함수로 컴파일
var RentalHistory = mongoose.model('RentalHistory', rentalHistory);

// 8. Student 객체를 new 로 생성해서 값을 입력
var rh1 = new RentalHistory({
    user_id: "1871152",
    title: "드링킹",
    registration_Number: '0378593',
    rental_date: "2021/01/23",
    return_date: "2021/01/30"
});

var rh2 = new RentalHistory({
    user_id: "1771318",
    title: "야수가 간다1",
    registration_Number: '0654361',
    rental_date: "2021/01/23",
    return_date: "2021/01/30"
});

var rh3 = new RentalHistory({
    user_id: "1871159",
    title: "Coach",
    registration_Number: 'W121816',
    rental_date: "2021/01/23",
    return_date: "2021/01/30"
});

var rh4 = new RentalHistory({
    user_id: "1871292",
    title: "붉은 별",
    registration_Number: '0749273',
    rental_date: "2021/01/23",
    return_date: "2021/01/30"
});

var rh5 = new RentalHistory({
    user_id: "1871292",
    title: "드링킹",
    registration_Number: '0749273',
    rental_date: "2021/05/31",
    return_date: "2021/06/13"
});

// 9. 데이터 저장
rh1.save(function (error, data) {
    if (error) {
        console.log(error);
    } else {
        console.log('Saved!')
    }
});
rh2.save(function (error, data) {
    if (error) {
        console.log(error);
    } else {
        console.log('Saved!')
    }
});
rh3.save(function (error, data) {
    if (error) {
        console.log(error);
    } else {
        console.log('Saved!')
    }
});
rh4.save(function (error, data) {
    if (error) {
        console.log(error);
    } else {
        console.log('Saved!')
    }
});
rh5.save(function (error, data) {
    if (error) {
        console.log(error);
    } else {
        console.log('Saved!')
    }
});