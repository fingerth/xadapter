> RecyclerView适配器，链式结构使用。

> Kotlin,Androidx

> Step 1. Add the JitPack repository to your build file
> 
> Add it in your root build.gradle at the end of repositories:

	
```
allprojects {
		repositories {
			...
			maven { url 'https://jitpack.io' }
		}
	}
```

> Step 2. Add the dependency

	
```
dependencies {
    implementation 'com.github.fingerth:xadapter:1.0.0'
}
```


### 1.简单使用
> 构造器 -> 数据 -> 布局 -> 布局设置 -> 创建
> 
> Xadapter() -> data() -> layoutId() -> bind() -> create()

```
    //val rv:RecyclerView 
    rv.adapter = Xadapter<RvDataBean>(this)
                .data(data)
                .layoutId(R.layout.item_rv_text_view)
                .bind { _, holder, _, bean, _ -> holder.setText(R.id.tv, bean.text) }
                .create()
           
```

### 2.不同Item样式的使用

> 构造器 -> 数据 -> 样式 -> 布局1 -> 布局2 ->... -> build() -> 布局设置1 -> 布局设置2 -> ... -> 创建

```
    //val rv:RecyclerView 
     
    rv.adapter = Xadapter<RvDataBean>(this)
                .data(data)
                .ViewTypeBuider()
                .typeBy { it.type }
                .typeItem(1 to R.layout.item_rv_text_view)
                .typeItem(2 to R.layout.item_rv_text_view2)
                .typeItem(3 to R.layout.item_rv_text_view3)
                .typeItem(R.layout.item_rv_default_view)//默认布局
                .build()
                .bind(1) { _, holder, _, bean, _ -> holder.setText(R.id.tv, bean.text) }
                .bind(2) { _, holder, _, bean, _ -> holder.setText(R.id.tv, bean.text) }
                .bind(3) { _, holder, _, bean, _ -> holder.setText(R.id.tv, bean.text) }
                .bind { _, _, _, _, _ -> }//默认布局
                .create()

```
    

### 3.使用动画 / 点击事件 


```
    //val rv:RecyclerView 
    rv.adapter = Xadapter<RvDataBean>(this)
                .data(data)
                .layoutId(R.layout.item_rv_text_view)
                .itemAnimation()
                .bind { _, holder, _, bean, _ -> holder.setText(R.id.tv, bean.text) }
                .itemClickListener { _, _, _, bean, _ ->
                    Toast.makeText(this, bean.text, Toast.LENGTH_SHORT).show()
                }
                .create()
```

### 4.自定义动画

```
    .itemAnimation(
        object : ImAnimation {
            //自定义动画
            override fun getAnimators(view: View?): Array<Animator> {
                return super.getAnimators(view)
            }
            //AnModel.NORMAL =普通, AnModel.ONLY_DOWN,AnModel.ONLY_UP  =只有上拉或下拉时才有动画
            override fun getAnModel(): AnModel = AnModel.ONLY_DOWN
            //从第几个开始有动画
            override fun getStart(): Int = 5
        }
    )
```

