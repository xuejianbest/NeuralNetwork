# NeuralNetwor in Java
A neural nets implementation only based on jdk that does not depend on any other libraries.

---

使用[MNIST数据集](http://yann.lecun.com/exdb/mnist/)进行训练和测试。

训练的模型为单隐藏层，输入为`(28*28)/(2*2)`个像素（我对原始图片进行了简单压缩：紧邻的四个像素只取左上角的一个像素），
隐藏层`30`个神经元，输出层`10`个神经元。

模型在测试集上的识别准确率为：93.32%（对测试集前5000和后5000样本的识别准确率分别为90.90%和95.74%）

模型信息已保存在`model/model.txt`

红色为识别错误的样本参考：
![识别测试](https://github.com/xuejianbest/images/blob/master/NN/test.png)
