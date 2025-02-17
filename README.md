## RcGacha
Gacha plugin for rcpve

## 設定ファイル
```yml
rarities:
  "1":
    base: 10
    start: 70
    end: 100
    mmSkillName: "single"
    mmSkillNameForBulk: "bulk"
    items:
      "stone": 1
  "2":
    base: 10
    start: 70
    end: 100
    mmSkillName: "single"
    mmSkillNameForBulk: "bulk"
    items:
      "stone": 1
  "3":
    base: 10
    start: 70
    end: 100
    mmSkillName: "single"
    mmSkillNameForBulk: "bulk"
    items:
      "stone": 1
```
こんな形のymlファイルが、plugins/RcGacha/gacha/_example.ymlに保存されます。

## 設定方法
1. 新しいガチャファイルを作成する
   - これを複製する
   - `/rcgacha new-gacha <ガチャ名>`を実行する
2. 変更する

    | プロパティ名             | 内容                                |
    |--------------------|-----------------------------------|
    | base               | そのレアリティの基礎確率                      |
    | start              | 天井開始の回転数                          |
    | end                | 天井終了の回転数                          |
    | mmSkillName        | 単発でそのレアリティが出た時の演出(MMのスキルId)       |
    | mmSkillNameForBulk | 10連等でそのレアリティが出た時の演出(MMのスキルId)     |
    | items              | そのレアリティの排出アイテム (`<アイテムId>: <重み>`) |

3. `/rcgacha load-gacha <ファイル名(.ymlなし)>`を実行して、新しいガチャの設定を反映する。
   - 一緒に全部をリロードしたいときは、`/rcgacha reload gacha`
4. おしまい