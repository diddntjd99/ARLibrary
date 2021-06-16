# ARLibrary
1. 프로젝트 수행 목적
 1.1. 프로젝트 정의
 증강현실을 이용한 도서관 스마트 시스템 서비스
 1.2. 프로젝트 배경
 도서관이나 서점에서 원하는 책을 찾을 때는 보통 도서 검색 컴퓨터에 검색 
 을 한 후 스스로 찾는 경우가 많다. 만약 혼자 찾지 못하는 경우 직원의 도 움 
 을 받아야 되는 경우도 있다. 이런 경우 직원의 도움없이 사용자 스스로 원하는 도서를 검색하고 찾을 수 
 있도록 증강현실을 이용하여 자신의 핸드폰 카메라로 도서 내비게이션 시스템 
 을 제공하여, 정 확하고 빠르게 원하는 도서를 찾을 수 있을 것이다. 그리고 도서 예약, 도서관 내 다른 서비스들을 APP에서 자유롭게 이용 할 수 
 있을 것이다
 1.3. 프로젝트 목표
 1) AR 내비게이션
 유니티에서 뷰포리아, Google AR과 NavMeshAgent를 활용하여 현실 세계 
 에서 캐릭터가 AR화면에 등장해 원하는 도서의 위치까지 안내해주도록 구현
 2) AR 이미지 인식
 유니티 뷰포리아, Google AR을 활용하여 안내받아 도착한 서가 앞에서 찾 
 고 있는 책이 위치에 없는 경우 카메라로 서가를 인식하 면 원하는 도서의 위 
 치를 알려주도록 구현
 3) App 서비스
 Android Studio로 도서관의 책 잔여 수, 대여 여부, 반납 알림, 리뷰 등 여 
 러가지 도서관 서비스를 제공하도록 구현2. 프로젝트 결과물의 개요

2. 참고 자료
 2.1. Mongo DB 사용
  https://medium.com/@pakss328/mongodb-%EC%84%A4%EC%B9%98-%EB%B0%8F-%EC%82%AC%EC%9A%A9%EB%B0%A9%EB%B2%95-%EA%B8%B0%EB%B3%B8%EB%B6%80%ED%84%B0-index%EA%B9%8C%EC%A7%80-dac5363eaa4f
 2.2. Node.js 웹서버 구축 및 배포
  1). Node.js 웹서버 구축
   https://www.zerocho.com/category/NodeJS/post/57774a8eacbd2e9803de0195
  2). Node.js 웹서버 배포
   https://dejavuqa.tistory.com/378 (npm init)
   https://juicybrainjello.blogspot.com/2018/11/nodejs-apache-on-windows.html (외부포트 사용방법)
   https://m.blog.naver.com/PostView.nhn?blogId=zetezz&logNo=221224911338&proxyReferer=https:%2F%2Fwww.google.com%2F (공유기 포트포워딩 방법)
 2.3. 안드로이드와 Node.js Socket.io 통신
  https://m.blog.naver.com/PostView.nhn?blogId=mym0404&logNo=221344144643&categoryNo=70&proxyReferer=&proxyReferer=https:%2F%2Fwww.google.com%2F
 2.4. 유니티 뷰포리아 임포트
  https://library.vuforia.com/articles/Training/getting-started-with-vuforia-in-unity.html#installing
 2.5. 유니티 NavMeshAgent
  https://m.blog.naver.com/PostView.nhn?blogId=gold_metal&logNo=220511730779&proxyReferer=https:%2F%2Fwww.google.com%2F
 2.6. 유니티 NavMeshAgent에서 런타임 시에 Navigation을 Bake하는 방법
  https://hannom.tistory.com/174
 2.7. 안드로이드 내에 유니티 프로젝트 적용
  https://kapella.tistory.com/14
  https://yoyostudy.tistory.com/12 (안드로이드 스튜디오에서 유니티로 데이터 보내기)
 2.8. 애니메이션 적용하는 방법
  https://wikidocs.net/91346
 2.9. 유니티 매뉴얼
  https://docs.unity3d.com/kr/2019.4/Manual/UnityManual.html
