# 2015_jdbc_practice
JDBCに関する演習問題です。

## 前提条件
- アプリケーションが動作するDBはMySQLを前提としています。
  - MySQLがなければ`build.gradle`を修正してH2DBを利用してください。
- 演習問題は【A・Bクラス】のものを採用しています。

## アプリケーション動作方法
- こちらのプロエジェクトをローカルにcloneします。
- 直下にある`gradlew`（Windowsは`gradlew.bat`？）を引数なしで実行するとアプリケーションが起動します。
  - DBのマイグレーションが自動的に実行されます。個別で実行する場合は`gradlew flywayMigrate`を実行してください。
- アプリケーション実行時にはDB接続情報を環境変数から読み込みます。以下の環境変数を設定してください。

|変数|説明|例|
|----|---|---|
|DB_URL|JDBC接続文字列です|jdbc:mysql://localhost/testdb|
|DB_USER|DBの接続ユーザです|scott|
|DB_PASSWORD|DBの接続パスワードです|tiger|

- `gradlew eclipse`を実行するとEclipseプロジェクトになるので、importしてから実行しても良いです。

## ソースの歩き方
- アプリケーションの実行や初期化などは`build.gradle`を見ましょう。タスクの定義が書かれています。（詳細はGradleを調べよう）
- DBのマイグレーションについては`src/resources/db`配下のSQLファイルを見ましょう。（詳細はFlywayを見ましょう）
- アプリケーションロジックは`build.gradle`にある`mainClassName`にあるクラスを見ましょう。そこから辿れます。

## 演習問題
### 講義で作成したテーブル
```sql
create table test (id int(11) primary key auto_increment, name varchar(255) not null);
```

### A・Bクラス
- Testテーブルに以下のカラムを追加しなさい。 

  1. update_date 情報更新日時(yyyy-MM-dd HH:MM:ss)
_※デファクトスタンダードに則るでも、日本形式の「yyyy/MM/dd HH:mm:ss」でも良い。_
  1. mail メールアドレス(hoge@gmail.com)
  1. tel 電話番号(0906000000) 
  1. is_deleted 削除フラグ(tinyint) デフォルト 0 (1で削除フラグが立つ)

- Testテーブルへの以下のDML処理をjavaで実現させなさい。 
  1. TestテーブルへのINSERT
  2. TestテーブルへのDELETE(論理削除)
  3. TestテーブルへのSELECT ALL
  4. TestテーブルへのSELECT WHERE
  5. TestテーブルへのUPDATE

上記DMLは標準入力より選択が可能とする。 
ユーザー操作はユーザーが「q」を選択するまで要求し続ける。
削除処理は論理削除とするが、システム起動時にis_deletedが1のデータを物理削除する機能を設けること。
※削除時に確認は求めないものとする。
削除結果のデータを標準出力して、ユーザーに知らせること。

実行例)`こちらは入力値` _こちらは出力結果_
`java JdbcTest`
_操作を選択してください。_
_(i: insert / d: delete / a: select all / s: select where / u: update/ q: exit)_
`i`
_name:_`太郎`
_mail:_`hoge@active.co.jp`
_tel:_`09050505555`
_太郎を登録しました。_
_操作を選択してください。_
_(i: insert / d: delete / a: select all / s: select where / u: update/ q: exit)_
_d_
_削除したいIDを入力してください。_
`12`
_ID12を削除しました。_
_操作を選択してください。_
_(i: insert / d: delete / a: select all / s: select where / u: update/ q: exit)_
`a`
_1 太郎 hoge@active.co.jp 09050505555 2014/04/12_
_2 次郎 fuga@active.co.jp 09060606666 2014/04/11_
_3 高志 takashi@active.co.jp 09060406444 2013/04/11_
_総勢: 3名_
_操作を選択してください。_
_(i: insert / d: delete / a: select all / s: select where / u: update/ q: exit)_
`s`
_検索したい名前を入力してください_
`太`
_1 太郎 hoge@active.co.jp 09050505555 2014/04/12_
_操作を選択してください。_
_(i: insert / d: delete / a: select all / s: select where / u: update/ q: exit)_
`u`
_更新対象IDを入力してください。_
`210009`
_210009 江口 egu@active.co.jp 09060001795 2015/04/11_
_name:_`江口`
_mail:_`test@active.co.jp`
_tel_:`09060001795`
_更新処理が完了しました。_
_210009 江口 test@active.co.jp 09060001795 2015/04/11_

_操作を選択してください。_
_(i: insert / d: delete / a: select all / s: select where / u: update/ q: exit)_
`q`
_システムを終了します。_


### Cクラス
- Testテーブルに以下のカラムを追加しなさい。 
  1. update_date 情報更新日(yyyy-MM-dd)
  2. mail メールアドレス(hoge@gmail.com)
  3. tel 電話番号(0906000000) 
  4. is_deleted 削除フラグ(tinyint) デフォルト 0 (1で削除フラグが立つ)
- Testテーブルへの以下のDML処理をjavaで実現させなさい。 
  1. TestテーブルへのINSERT
  2. TestテーブルへのDELETE(物理削除)
  3. TestテーブルへのSELECT ALL
  4. TestテーブルへのSELECT WHERE

上記DMLは標準入力より選択が可能とする。 
ユーザー操作はユーザーが「q」を選択するまで要求し続ける。

実行例)`こちらは入力値` _こちらは出力結果_
`java JdbcTest`
_操作を選択してください。_
_(i: insert / d: delete / a: select all / s: select where / q: exit)_
`i`
_name:_`太郎`
mail: hoge@active.co.jp
_tel: `09050505555`
_太郎を登録しました。_
_操作を選択してください。_
_(i: insert / d: delete / a: select all / s: select where / q: exit)_
`d`
_削除したいIDを入力してください。_
`12`
_ID12を削除しました。_
_操作を選択してください。_
_(i: insert / d: delete / a: select all / s: select where / q: exit)_
`a`
_1 太郎 hoge@active.co.jp 09050505555 2014/04/12_
_2 次郎 fuga@active.co.jp 09060606666 2014/04/11_
_3 高志 takashi@active.co.jp 09060406444 2013/04/11_
_総勢: 3名_
_操作を選択してください。_
_(i: insert / d: delete / a: select all / s: select where / q: exit)_
`s`
_検索したい名前を入力してください_
`太`
_1 太郎 hoge@active.co.jp 09050505555 2014/04/12_

`操作を選択してください。`
_(i: insert / d: delete / a: select all / s: select where / q: exit)_
`q`
_システムを終了します。_


### 指針
- 目安時間
  - A・B
    - 4時間
  - C
    - 8時間

### 納品物
- ソースコード
- 単体テスト仕様書
- 演習問題チェックリスト
- 納品書
