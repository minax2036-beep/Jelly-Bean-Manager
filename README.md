# Jelly Bean Manager

Android 9+向けのJelly Bean Digital Signage管理アプリ。Javaで開発し、UIとローカル機能を先に整え、Signageとの送受信は最後に実装する。

## 現在の実装
- ダークテーマのホーム画面
- ホーム / 端末 / コンテンツ / プレイリスト / 予定 の画面切り替え
- 端末・コンテンツ・プレイリスト・予定のローカル追加、編集、削除
- SharedPreferencesへのJSON保存
- GitHub ActionsによるDebug APKビルド

初期データはUI確認用のサンプルです。通信・QRスキャン・メディアファイル転送は未実装です。

## ビルド
JDK 17とAndroid SDKがある環境で:

```bash
gradle assembleDebug
```

APK: `app/build/outputs/apk/debug/app-debug.apk`

## ドキュメント
- [仕様書](docs/SPECIFICATION.md)
- [通信プロトコル案](docs/PROTOCOL.md)
- [プレイリストとスケジュール](docs/PLAYLIST.md)
