# 2015_jdbc_practice
JDBCに関する演習問題です。

## 前提条件
- アプリケーションが動作するDBはMySQLを前提としています。
  - MySQLがなければ`build.gradle`を修正してH2DBを利用してください。

## 動作方法
- こちらのプロエジェクトをローカルにcloneします。
- 直下にある`gradlew`（Windowsは`gradlew.bat`？）を実行するとアプリケーションが起動します。
- アプリケーション実行時にはDB接続情報を環境変数から読み込みます。以下の環境変数を設定してください。

|変数|説明|例|
|----|---|---|
|DB_URL|JDBC接続文字列です|jdbc:mysql://localhost/testdb|
|DB_USER|DBの接続ユーザです|scott|
|DB_PASSWORD|DBの接続パスワードです|tiger|
  
- `gradlew eclipse`を実行するとEclipseプロジェクトになるので、importしてから実行しても良いです。