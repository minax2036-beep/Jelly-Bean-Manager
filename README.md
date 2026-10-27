# Jelly Bean Manager

Android 9+向けのJelly Bean Digital Signage管理アプリ。Javaで開発し、UIとローカル機能を先に整え、Signageとの送受信は最後に実装する。

## 現在の実装

- ダークテーマのホーム画面
- ホーム / 端末 / コンテンツ / プレイリスト / 予定 の画面切り替え
- 端末・コンテンツ・プレイリスト・予定のローカル追加、編集、削除
- SharedPreferencesへのJSON保存
- GitHub ActionsによるDebug APKの自動ビルド
- GitHub Secretsを使ったRelease APKの署名・自動ビルド（Secrets設定後）

初期データはUI確認用のサンプルです。通信・QRスキャン・メディアファイル転送は未実装です。

## APKをダウンロードする

GitHubリポジトリの **Actions** → 最新の **Android CI** 実行 → **Artifacts** から取得します。

- `jelly-bean-manager-debug`: デバッグ用APK。毎回ビルドされます。
- `jelly-bean-manager-release`: リリース署名済みAPK。署名用Secretsを設定した後、mainへのpushまたは手動実行で生成されます。

Pull Requestでは秘密鍵を使わず、Debug APKのみビルドします。

## リリース署名キーの作成

Chromebook Linux（ターミナル）でJDK 17を利用できる状態にして、リポジトリ外の安全なディレクトリで実行します。パスワードは自分で決め、忘れないようパスワードマネージャーなどに保管してください。

```bash
keytool -genkeypair -v \
  -keystore jelly-bean-manager-release.jks \
  -alias jellybeanmanager \
  -keyalg RSA -keysize 4096 -validity 10000
```

入力が完了したら、キーストアのファイルが作成されたことを確認します。

```bash
ls -l jelly-bean-manager-release.jks
```

**重要:** `jelly-bean-manager-release.jks` はGitHubリポジトリにコミットしないでください。紛失すると、同じ署名証明書でRelease APKを更新できなくなる可能性があります。安全な別の場所にもバックアップを保管してください。

## GitHub Secretsの設定

1. [Jelly-Bean-ManagerのActions Secrets設定](https://github.com/minax2036-beep/Jelly-Bean-Manager/settings/secrets/actions) を開きます。
2. **New repository secret** から、以下の4つを1つずつ登録します。

| Secret名 | 値 |
| --- | --- |
| `ANDROID_KEYSTORE_BASE64` | キーストアファイルをBase64化した文字列 |
| `ANDROID_KEYSTORE_PASSWORD` | keytoolで設定したキーストアのパスワード |
| `ANDROID_KEY_ALIAS` | `jellybeanmanager` |
| `ANDROID_KEY_PASSWORD` | keytoolで設定したキーのパスワード |

Base64文字列はLinuxターミナルで生成できます。

```bash
base64 -w 0 jelly-bean-manager-release.jks
```

表示された文字列をコピーし、`ANDROID_KEYSTORE_BASE64` のSecret値に直接貼り付けます。**この文字列やパスワードをチャット、Issue、ソースコードに貼り付けないでください。**

keytoolがキーストアとキーで同じパスワードを設定した場合、`ANDROID_KEYSTORE_PASSWORD` と `ANDROID_KEY_PASSWORD` には同じ値を登録します。

Secrets登録後、Actionsタブから **Android CI** を開き、**Run workflow** で手動実行します。成功するとDebugとReleaseの両方のArtifactが生成されます。設定が不足している場合はDebug APKのみ生成され、Release APKはスキップされます。

## ローカルビルド

JDK 17とAndroid SDKがある環境で:

```bash
gradle assembleDebug
```

APK: `app/build/outputs/apk/debug/app-debug.apk`

## ドキュメント

- [仕様書](docs/SPECIFICATION.md)
- [通信プロトコル案](docs/PROTOCOL.md)
- [プレイリストとスケジュール](docs/PLAYLIST.md)
