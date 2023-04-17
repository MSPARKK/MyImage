# MyImage

이미지를 검색해서 보관함에 수집하는 안드로이드 앱

- 목적 : Kotlin + Corountine + MVVM + UnitTest 활용 연습
- 작업 기간 : 23.03
- 구현 기능
    - Kakao API를 사용하여 이미지 검색 결과 불러오기
        - 두 가지 API(이미지 검색과 동영상 썸네일 검색)를 Corountine awaitAll를 사용하여 결과를 모두 받고 처리할 수 있게함.
        - 두 가지 API의 결과 이미지들이 시간 순으로 보여지게 하기위해 두 Queue를 통해 최신 순으로 정렬
    - 검색된 이미지에 좋아요를 하면 Shared Preference에 저장
        - 이미 저장된 이미지는 검색 결과에도 하트가 표시되게 처리
    - UnitTest
        - MainViewModel UnitTest - 사진 검색 결과 로직, 이미 저장된 사진 불러오기 로직 테스트
        - Util 함수 UnitTest  - 사진의 datetime을 처리하는 로직 테스트, Android OS 버전에 따른 테스트 처리

- 활용한 라이브러리
    - Retrofit2, okhttp3
    - Mockito
    - Glide
    - Android Jetpack (ViewModel, LiveData)
