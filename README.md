# CimoShop
作者：incimo  
时间：2020/06/14
### Cimo画廊 v1.0.1 beta 版本

> #### 更新内容

- 2020/06/19 将购物车Item间距修改为1sp

- 底部导航栏毛玻璃效果的实现思路已经有了，不过经过研究可能需要修改ViewPage中3个相关Fragment的布局，比较耗时，只能等有时间再做了

> #### UI设计标准

- Material Design 谷歌官方材质设计语言 [Material Design](https://www.material.io/)
- Material Design 在线主题色配置工具 [Material Design：color](https://material.io/resources/color/#!/?view.left=0&view.right=0)

> #### 功能特点

- 瀑布流布局
- 下拉刷新
- 分页加载
- 网络错误处理
- 在线搜索
- 购物车滑动删除，~~商品元素拖拽排序~~
- Github OAuth授权登录
- 支付宝沙箱环境支付

> #### 使用到的框架

- photoView 图片操作 ：[PhotoView](https://github.com/chrisbanes/PhotoView)
- BaseRecyclerViewAdapterHelper（RecyclerView开源框架）：[BRVAH](http://www.recyclerview.org/)
- Volley 网络请求 ：[Volley](https://github.com/google/volley)
- Swiperefreshlayout 滑动刷新
- Glide 网络图片加载 ：[Glide](https://bumptech.github.io/glide/)
- Shimmerlayout 图片加载动画 ：[Shimmerlayout](https://github.com/team-supercharge/ShimmerLayout)
- PictureSelector 图片选择器 ：[PictureSelector](https://github.com/LuckSiege/PictureSelector)

> #### 题外话

&emsp;&emsp;人生的第一个Android综合项目,最大的收货就是在开发过程中对Android开发的理解得以加深。

- 在开发Android的同时，结合Vue来学习，发现很多思想上的共同之处，比如Android中的UI界面和HTML中的UI界面都是树型的，只要通过当前节点找到相关的父节点，子节点或兄弟节点，就能对其进行操作
- Android生命周期和Vue中的生命周期很类似，使用起来非常的方便还加深了对AOP思想的理解
- 在UI设计方面实践了Material Design的使用，UI设计相关知识得到了补充（毕竟如果做出个丑的东西感觉很难受...）

&emsp;&emsp;另外还发现了一个有趣的事情，Kotlin和ES6以及Python的语法有很多相似之处...是不是以后开发语言都是这样式的了？

&emsp;&emsp;感谢b站上的大神：[longway777](https://www.bilibili.com/video/BV1w4411t7UQ?p=1) 大神的视频确实是不可多得的好教材，给了我很多灵感和帮助

> #### 结

&emsp;&emsp;开发技术迭代的越来越快，也越来越简单，不过还是得掌握正确的学习方法，学习方法掌握了，技术怎么更新都不怕。希望有一天我也能成为造轮子的人~
