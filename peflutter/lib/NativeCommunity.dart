import 'package:flutter/material.dart';
import 'video.dart';
import 'package:flutter/services.dart';

class CommunityApp extends StatelessWidget {
  const CommunityApp({Key? key}) : super(key: key);

  // This widget is the root of your application.
  @override
  Widget build(BuildContext context) {
    return MaterialApp(
      title: 'Community',
      theme: ThemeData(
        primarySwatch: Colors.blue,
      ),
      home: const CommunityPage(title: 'community with native'),
    );
  }
}

class CommunityPage extends StatefulWidget {
  const CommunityPage({Key? key, required this.title}) : super(key: key);

  final String title;

  @override
  State<CommunityPage> createState() => _CommunityPageState();
}

class _CommunityPageState extends State<CommunityPage> {
  static const nativeCommunityChannel = MethodChannel("native_method_channel");
  String batteryLevel = 'unknown level';

  Future<void> getBatteryLevel() async {
    setState(() {
      batteryLevel = 'fetching...';
    });
    try {
      final int result =
          await nativeCommunityChannel.invokeMethod('getBatteryLevelAsync');
      batteryLevel = 'Battery level at $result % .';
    } on PlatformException catch (e) {
      batteryLevel = "Failed to get battery level: '${e.message}'.";
    }
    setState(() {
      batteryLevel = batteryLevel;
    });
  }
  Future<dynamic> nativeCallMethod(MethodCall call) async {
    switch (call.method) {
      case "getFlutterMethod":
        return "This is a flutter method";
      default:
        return "can't find flutter method";
    }
  }
  @override
  Widget build(BuildContext context) {
    nativeCommunityChannel.setMethodCallHandler(nativeCallMethod);
    return Scaffold(
      appBar: AppBar(
        title: Text(widget.title),
      ),
      body: Container(
        padding: const EdgeInsets.all(32),
        child: Column(
          children: [
            GestureDetector(
              child: Container(
                height: 50.0,
                padding: const EdgeInsets.all(8.0),
                margin: const EdgeInsets.symmetric(horizontal: 8.0),
                decoration: BoxDecoration(
                  borderRadius: BorderRadius.circular(5.0),
                  color: Colors.lightGreen[500],
                ),
                child: const Center(
                  child: Text(
                    'start get battery',
                  ),
                ),
              ),
              onTap: () {
                getBatteryLevel();
              },
            ),
            Text(
              '$batteryLevel',
              style: Theme.of(context).textTheme.headline4,
            ),
          ],
        ),
      ),
    );
  }
}
