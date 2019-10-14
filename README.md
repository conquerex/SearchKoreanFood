# SearchKoreanFood
이미지와 비디오 리스트를 볼 수 있는 Sample App

## 진행완료된 미션
1. RecyclerView 를 사용하여 이미지 갤러리 만들기.
- Restful API 를 사용 : flickr API 사용
- 통신은 오픈소스 라이브러리 사용 가능 (OkHttp, Retrofit 사용)

2. VideoView 기능 추가.
- 1번에서 구현한 RecyclerView에 Video를 scroll 상태에 따라 재생하거나 정지할 수 있는 cell을 추가
- RecyclerView를 scroll하여 Video View가 화면에 나타났을 때, cell 내부의 Video View를 재생
- RecyclerView를 scroll하여 Video View가 화면에서 사라졌을 때, cell 내부의 Video View를 정지
- Video View는 오픈소스 라이브러리 사용 가능 (exoPlayer 사용)
- 보여야하는 Video file의 url을 사용. 내용은 자유롭게 사용 가능
- 영상을 file 형태로 앱 내부에 저장하는 것은 불가

## 이행하지 못한 미션
* 디스크 캐시 기능 구현

## 개선 사항
* 디자인패턴 적용
* 앱이 백그라운드로 나올 때, 플레이어 중지
* * Service와 Notification으로 ExoPlayer 제어
* 영상이 화면 바깥으로 나갔으나 영상은 몇초 후 종료가 됨
* * 멈추는 방식이 잘못되었거나
* * 화면 이탈을 제대로 인지하지 못하는 상황

## 시연 영상 (나는 되는데요 방지)
[https://github.com/conquerex/SearchKoreanFood/blob/master/video_searchKoreanFood.mp4](https://github.com/conquerex/SearchKoreanFood/blob/master/video_searchKoreanFood.mp4)
