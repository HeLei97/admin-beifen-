# LayUI拓展组件：IconPicker图标选择器

#### 项目介绍
基于Layui的图标选择器，支持查询、分页、点击回调等功能，提供手动选择图标事件。注：图标来自于Layui2.4.1 

#### 效果图
![输入图片说明](https://images.gitee.com/uploads/images/2018/0904/182114_7fbd07ab_1157021.gif "icon-a.gif")
![输入图片说明](https://images.gitee.com/uploads/images/2018/0830/235644_7c9ab176_1157021.gif "b.gif")
![输入图片说明](https://images.gitee.com/uploads/images/2018/0830/235651_a9f30be3_1157021.gif "c.gif")

#### 使用示例

```
    <input type="text" id="iconPicker" lay-filter="iconPicker" class="layui-input">
```

```
    layui.use(['iconPicker'], function () {
        var iconPicker = layui.iconPicker;
            
            iconPicker.render({
                // 选择器，推荐使用input
                elem: '#iconPicker',
                // 数据类型：fontClass/unicode，推荐使用fontClass
                type: 'fontClass',
                // 是否开启搜索：true/false
                search: true,
                // 是否开启分页
                page: true,
                // 每页显示数量，默认12
                limit: 12,
                // 点击回调
                click: function (data) {
                    console.log(data);
                }
            });

            /**
             * 选中图标 （常用于更新时默认选中图标）
             * @param filter lay-filter
             * @param iconName 图标名称，自动识别fontClass/unicode
             */
            iconPicker.checkIcon('iconPicker', 'layui-icon-star-fill');

    });
```
ps. iconPicker.js文件在module/iconPicker目录中

#### 更新日志
- 2018.09.25：
  - 添加演示页面demo.html
  - 添加无数据提示
  - 修复下拉三角无法点击的bug
  - 修复点击搜索输入框后选择器关闭的bug
  - 其他的优化

#### 注意事项
1. 推荐使用fontClass