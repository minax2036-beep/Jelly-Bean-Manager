# 通信プロトコル案（未実装）

通信はUIとローカル機能が安定してから実装する。このアプリは現在ネットワーク通信を行わない。

## エンドポイント案
- GET /api/v1/status
- GET /api/v1/device
- POST /api/v1/pair
- POST /api/v1/playlist
- POST /api/v1/media
- POST /api/v1/schedule
- POST /api/v1/command

JSONは端末情報・設定・プレイリスト等に、画像・動画は別途ファイル転送を使用する。

## ペアリング
1. Signage側で一時的なペアリング情報を表示
2. ManagerがQRコードを読み取る
3. 初回認証後、端末IDと認証情報を安全に保存
4. LAN内で端末を再発見し、IP変更後も端末IDで識別

## オフライン
受信済みコンテンツをSignage側に保存し、接続断でも再生を続ける。

## セキュリティ
平文HTTPで長期トークンを送らない。Android 4.2.2側のTLS互換性を調査して認証方式を決める。
