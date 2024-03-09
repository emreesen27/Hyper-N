import 'package:explode/channel_bridge.dart';
import 'package:youtube_explode_dart/youtube_explode_dart.dart';

class Explode {
  final _explode = YoutubeExplode();
  static late VideoSearchList? videos;

  search(String query) async {
    videos = await _explode.search.search(query);
    List<Map<String, String>> videoList = await _fetchVideoList(videos);

    await ChannelBridge.instance.channel
        .invokeMethod('receiveSearchData', {'data': videoList});
  }

  nextPage() async {
    List<Map<String, String>> videoList = [];
    videos = await videos?.nextPage();

    if (videos != null) {
      for (var video in videos!) {
        Map<String, String> videoData = {
          "id": video.id.value,
          'title': video.title,
          'description': video.description,
          "author": video.author,
          'url': video.url,
          "duration": video.duration?.inMilliseconds.toString() ?? "0",
          "thumbnails": video.thumbnails.standardResUrl,
        };
        videoList.add(videoData);
      }
    }

    await ChannelBridge.instance.channel
        .invokeMethod('receiveSearchData', {'data': videoList});
  }

  getAudioUrl(String videoId) async {
    var manifest =
        await _explode.videos.streamsClient.getManifest(VideoId(videoId));
    String url = manifest.audioOnly.last.url.toString();

    await ChannelBridge.instance.channel
        .invokeMethod('receiveAudioUrl', {'data': url});
  }

  void dispose() {
    _explode.close();
    videos = null;
  }

  Future<List<Map<String, String>>> _fetchVideoList(
      VideoSearchList? videos) async {
    List<Map<String, String>> videoList = [];

    if (videos != null) {
      for (var video in videos) {
        Map<String, String> videoData = {
          "id": video.id.value,
          'title': video.title,
          'description': video.description,
          "author": video.author,
          'url': video.url,
          "duration": video.duration?.inMilliseconds.toString() ?? "0",
          "thumbnails": video.thumbnails.standardResUrl,
        };
        videoList.add(videoData);
      }
    }

    return videoList;
  }
}
