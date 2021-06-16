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
var user = mongoose.Schema({
	user_id: String, //유저 id
    passwd: String, //유저 비밀번호
	name: String //이름
});

// 7. 정의된 스키마를 객체처럼 사용할 수 있도록 model() 함수로 컴파일
var User = mongoose.model('User', user);

// 8. Student 객체를 new 로 생성해서 값을 입력
var u1 = new User({
	user_id: "1871152",
	passwd: "1871152",
	name: "양우성"
});

var u2 = new User({
	user_id: "1771318",
	passwd: "1771318",
	name: "김사라"
});

var u3 = new User({
	user_id: "1871159",
	passwd: "1871159",
	name: "오아람"
});

var u4 = new User({
	user_id: "1871292",
	passwd: "1871292",
	name: "허예원"
});

// 9. 데이터 저장
u1.save(function (error, data) {
    if (error) {
        console.log(error);
    } else {
        console.log('Saved!')
    }
});

u2.save(function (error, data) {
    if (error) {
        console.log(error);
    } else {
        console.log('Saved!')
    }
});

u3.save(function (error, data) {
    if (error) {
        console.log(error);
    } else {
        console.log('Saved!')
    }
});

u4.save(function (error, data) {
    if (error) {
        console.log(error);
    } else {
        console.log('Saved!')
    }
});