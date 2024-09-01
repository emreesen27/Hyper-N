import 'package:explode/channel_bridge.dart';
import 'package:youtube_explode_dart/youtube_explode_dart.dart';

class Explode {
  final _explode = YoutubeExplode();
  static late VideoSearchList? videos;

  search(String query) async {
    videos = await _explode.search.search(query);
    List<Map<String, String>> videoList = _fetchVideoList(videos);

    await ChannelBridge.instance.channel
        .invokeMethod('receiveSearchData', {'data': videoList});
  }

  nextPage() async {
    videos = await videos?.nextPage();
    List<Map<String, String>> videoList = _fetchVideoList(videos);

    await ChannelBridge.instance.channel
        .invokeMethod('receiveNextSearchData', {'data': videoList});
  }

  getAudioUrl(String videoId) async {
    var manifest =
        await _explode.videos.streamsClient.getManifest(VideoId(videoId));
    String url = manifest.audioOnly.last.url.toString();

    await ChannelBridge.instance.channel
        .invokeMethod('receiveAudioUrl', {'data': url});
  }

  Future<void> getHighlights(List<String> highlights) async {
    List<Map<String, String>> videoList = [];

    List<Future<Video>> futures = [];
    for (var element in highlights) {
      futures.add(_explode.videos.get(element));
    }

    List<Video> videos = await Future.wait(futures);

    for (var video in videos) {
      videoList.add(_mapToVideo(video));
    }

    await ChannelBridge.instance.channel
        .invokeMethod('receiveHighlightsData', {'data': videoList});
  }

  List<Map<String, String>> _fetchVideoList(VideoSearchList? videos) {
    List<Map<String, String>> videoList = [];

    if (videos != null) {
      for (var video in videos) {
        videoList.add(_mapToVideo(video));
      }
    }

    return videoList;
  }

  _mapToVideo(Video video) {
    Map<String, String> videoData = {
      "id": video.id.value,
      'title': video.title,
      'description': video.description,
      "author": video.author,
      'url': video.url,
      "duration": video.duration?.inMilliseconds.toString() ?? "",
      "thumbnail": video.thumbnails.mediumResUrl,
      "publishYear": video.publishDate?.year.toString() ?? "",
      "uploadYear": video.uploadDate?.year.toString() ?? "",
      "viewCount": video.engagement.viewCount.toString(),
      "isLive": video.isLive.toString()
    };
    return videoData;
  }

  void dispose() {
    _explode.close();
    videos = null;
  }
}
