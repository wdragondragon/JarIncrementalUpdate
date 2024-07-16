### 使用方式

运行参数

- basePath：可不填，默认为使用当前jar的运行目录
- mode：不填为生成对比，填update为进行jar的更新

以下为参考目录结构：

```text
|-source
  |-test.jar            最新文件
  |-test_old.jar        旧版文件
|-update
  |-test                差异文件
    |-diff_report.txt   差异描述文件
    |-......            增量更新文件
|-target
  |-test                在测试/生产旧版文件的持续更新文件夹。（与source/test_old.jar内容一致）
  |-test.jar            根据差异更新最新jar

```

#### 一、生成增量差异文件

在FileComparison目录下创建source目录，将test.jar与test_old.jar放到该目录中。

运行 java -jar FileComparison-1.0-SNAPSHOT-jar-with-dependencies.jar 会对该source目录下的 test.jar 和 test_old.jar
进行以下操作

1. 先清空 source, update（上一次生成的差异信息）目录下的内容
2. 分别使用jar -xvf 命令解压到 source/test/ 和 source/test_old/ 目录下
3. 对source/test 和 source/test_old 进行文件差异对比。
4. 将对比结果上报到 update/test 中，其中 update/test/diff_report.txt 为差异描述文件。其他文件是需要替换的增量文件。

#### 二、差异更新

1. 在source和update的相对目录下，创建target目录，将旧版本的jar解压到target目录下。得到target/test。
2. 运行命令 java -jar -Dmode=update FileComparison-1.0-SNAPSHOT-jar-with-dependencies.jar。
3. 执行完毕后，会自动在target目录下生成 test.jar

