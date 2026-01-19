# 实现指南 (Implementation Guide)

## 待完成功能清单

本文档详细说明如何完成BookExchange项目的剩余功能。

---

## 1. BookAdapter实现

### 位置
`app/src/main/java/com/example/bookexchange/ui/adapter/BookAdapter.kt`

### 代码模板
```kotlin
package com.example.bookexchange.ui.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.bookexchange.R
import com.example.bookexchange.model.Book

class BookAdapter(
    private var books: List<Book>,
    private val onBookClick: (Book) -> Unit,
    private val onFavoriteClick: (Book) -> Unit
) : RecyclerView.Adapter<BookAdapter.BookViewHolder>() {

    class BookViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val bookImageView: ImageView = itemView.findViewById(R.id.bookImageView)
        val bookTitleTextView: TextView = itemView.findViewById(R.id.bookTitleTextView)
        val bookAuthorTextView: TextView = itemView.findViewById(R.id.bookAuthorTextView)
        val bookPriceTextView: TextView = itemView.findViewById(R.id.bookPriceTextView)
        val bookConditionTextView: TextView = itemView.findViewById(R.id.bookConditionTextView)
        val sellerNameTextView: TextView = itemView.findViewById(R.id.sellerNameTextView)
        val favoriteImageView: ImageView = itemView.findViewById(R.id.favoriteImageView)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BookViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_book, parent, false)
        return BookViewHolder(view)
    }

    override fun onBindViewHolder(holder: BookViewHolder, position: Int) {
        val book = books[position]
        
        holder.bookTitleTextView.text = book.title
        holder.bookAuthorTextView.text = book.author
        holder.bookPriceTextView.text = "¥ ${String.format("%.2f", book.price)}"
        holder.bookConditionTextView.text = book.condition
        holder.sellerNameTextView.text = "卖家: ${book.sellerName}"
        
        // Load image with Glide
        if (book.images.isNotEmpty()) {
            // Parse JSON array and load first image
            Glide.with(holder.itemView.context)
                .load(book.images)  // You'll need to parse the JSON array
                .placeholder(R.drawable.placeholder_book)
                .into(holder.bookImageView)
        }
        
        holder.itemView.setOnClickListener {
            onBookClick(book)
        }
        
        holder.favoriteImageView.setOnClickListener {
            onFavoriteClick(book)
        }
    }

    override fun getItemCount() = books.size

    fun updateBooks(newBooks: List<Book>) {
        books = newBooks
        notifyDataSetChanged()
    }
}
```

### 在Fragment中使用
```kotlin
// In BookListFragment
private lateinit var bookAdapter: BookAdapter

private fun setupRecyclerView() {
    bookAdapter = BookAdapter(
        books = emptyList(),
        onBookClick = { book ->
            // Navigate to BookDetailActivity
            val intent = Intent(requireContext(), BookDetailActivity::class.java)
            intent.putExtra("bookId", book.id)
            startActivity(intent)
        },
        onFavoriteClick = { book ->
            // Toggle favorite
            bookViewModel.toggleFavorite(book.id)
        }
    )
    
    booksRecyclerView.layoutManager = LinearLayoutManager(context)
    booksRecyclerView.adapter = bookAdapter
}
```

---

## 2. BookViewModel实现

### 位置
`app/src/main/java/com/example/bookexchange/viewmodel/BookViewModel.kt`

### 代码模板
```kotlin
package com.example.bookexchange.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.bookexchange.model.Book
import com.example.bookexchange.repository.BookRepository
import kotlinx.coroutines.launch

class BookViewModel(application: Application) : AndroidViewModel(application) {
    private val repository = BookRepository(application)
    
    val allBooks: LiveData<List<Book>> = repository.getAllAvailableBooks()
    
    private val _toastMessage = MutableLiveData<String?>()
    val toastMessage: LiveData<String?> = _toastMessage
    
    private val _isLoading = MutableLiveData<Boolean>(false)
    val isLoading: LiveData<Boolean> = _isLoading
    
    fun searchBooks(query: String): LiveData<List<Book>> {
        return repository.searchBooks(query)
    }
    
    fun getBooksByCategory(category: String): LiveData<List<Book>> {
        return repository.getBooksByCategory(category)
    }
    
    fun toggleFavorite(userId: Long, bookId: Long) {
        viewModelScope.launch {
            try {
                val isFavorite = repository.toggleFavorite(userId, bookId)
                _toastMessage.value = if (isFavorite) "已添加到收藏" else "已取消收藏"
            } catch (e: Exception) {
                _toastMessage.value = "操作失败: ${e.message}"
            }
        }
    }
    
    fun addBook(book: Book) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val bookId = repository.addBook(book)
                _toastMessage.value = "发布成功"
            } catch (e: Exception) {
                _toastMessage.value = "发布失败: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }
    
    fun incrementViewCount(bookId: Long) {
        viewModelScope.launch {
            repository.incrementViewCount(bookId)
        }
    }
    
    fun clearEvents() {
        _toastMessage.value = null
    }
}
```

---

## 3. AddBookActivity完整实现

### 布局文件
`app/src/main/res/layout/activity_add_book.xml`

```xml
<?xml version="1.0" encoding="utf-8"?>
<ScrollView xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:app="http://schemas.android.com/apk/res-auto"
    android:layout_width="match_parent"
    android:layout_height="match_parent"
    android:fillViewport="true">

    <LinearLayout
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        android:orientation="vertical"
        android:padding="16dp">

        <TextView
            android:layout_width="wrap_content"
            android:layout_height="wrap_content"
            android:text="发布书籍"
            android:textSize="24sp"
            android:textStyle="bold"
            android:layout_marginBottom="24dp" />

        <!-- Image picker section -->
        <LinearLayout
            android:layout_width="match_parent"
            android:layout_height="wrap_content"
            android:orientation="horizontal"
            android:layout_marginBottom="16dp">
            
            <ImageView
                android:id="@+id/bookImage1"
                android:layout_width="80dp"
                android:layout_height="80dp"
                android:layout_margin="4dp"
                android:background="@color/gray_light"
                android:scaleType="centerCrop" />
            
            <ImageView
                android:id="@+id/bookImage2"
                android:layout_width="80dp"
                android:layout_height="80dp"
                android:layout_margin="4dp"
                android:background="@color/gray_light"
                android:scaleType="centerCrop" />
                
            <com.google.android.material.button.MaterialButton
                android:id="@+id/addImageButton"
                android:layout_width="80dp"
                android:layout_height="80dp"
                android:layout_margin="4dp"
                android:text="+"
                android:textSize="32sp" />
        </LinearLayout>

        <com.google.android.material.textfield.TextInputLayout
            style="@style/EditText.Default"
            android:layout_width="match_parent"
            android:layout_height="wrap_content"
            android:layout_marginBottom="16dp"
            android:hint="@string/book_title">
            <com.google.android.material.textfield.TextInputEditText
                android:id="@+id/titleEditText"
                android:layout_width="match_parent"
                android:layout_height="wrap_content"
                android:inputType="text" />
        </com.google.android.material.textfield.TextInputLayout>

        <com.google.android.material.textfield.TextInputLayout
            style="@style/EditText.Default"
            android:layout_width="match_parent"
            android:layout_height="wrap_content"
            android:layout_marginBottom="16dp"
            android:hint="@string/book_author">
            <com.google.android.material.textfield.TextInputEditText
                android:id="@+id/authorEditText"
                android:layout_width="match_parent"
                android:layout_height="wrap_content"
                android:inputType="text" />
        </com.google.android.material.textfield.TextInputLayout>

        <com.google.android.material.textfield.TextInputLayout
            style="@style/EditText.Default"
            android:layout_width="match_parent"
            android:layout_height="wrap_content"
            android:layout_marginBottom="16dp"
            android:hint="@string/book_price">
            <com.google.android.material.textfield.TextInputEditText
                android:id="@+id/priceEditText"
                android:layout_width="match_parent"
                android:layout_height="wrap_content"
                android:inputType="numberDecimal" />
        </com.google.android.material.textfield.TextInputLayout>

        <!-- Condition Spinner -->
        <com.google.android.material.textfield.TextInputLayout
            style="@style/Widget.MaterialComponents.TextInputLayout.OutlinedBox.ExposedDropdownMenu"
            android:layout_width="match_parent"
            android:layout_height="wrap_content"
            android:layout_marginBottom="16dp"
            android:hint="@string/book_condition">
            <AutoCompleteTextView
                android:id="@+id/conditionAutoComplete"
                android:layout_width="match_parent"
                android:layout_height="wrap_content"
                android:inputType="none" />
        </com.google.android.material.textfield.TextInputLayout>

        <!-- Category Spinner -->
        <com.google.android.material.textfield.TextInputLayout
            style="@style/Widget.MaterialComponents.TextInputLayout.OutlinedBox.ExposedDropdownMenu"
            android:layout_width="match_parent"
            android:layout_height="wrap_content"
            android:layout_marginBottom="16dp"
            android:hint="@string/book_category">
            <AutoCompleteTextView
                android:id="@+id/categoryAutoComplete"
                android:layout_width="match_parent"
                android:layout_height="wrap_content"
                android:inputType="none" />
        </com.google.android.material.textfield.TextInputLayout>

        <com.google.android.material.textfield.TextInputLayout
            style="@style/EditText.Default"
            android:layout_width="match_parent"
            android:layout_height="wrap_content"
            android:layout_marginBottom="24dp"
            android:hint="@string/book_description">
            <com.google.android.material.textfield.TextInputEditText
                android:id="@+id/descriptionEditText"
                android:layout_width="match_parent"
                android:layout_height="wrap_content"
                android:inputType="textMultiLine"
                android:minLines="3"
                android:maxLines="5" />
        </com.google.android.material.textfield.TextInputLayout>

        <com.google.android.material.button.MaterialButton
            android:id="@+id/publishButton"
            style="@style/Button.Primary"
            android:layout_width="match_parent"
            android:layout_height="wrap_content"
            android:text="发布" />

        <ProgressBar
            android:id="@+id/progressBar"
            android:layout_width="wrap_content"
            android:layout_height="wrap_content"
            android:layout_gravity="center"
            android:layout_marginTop="16dp"
            android:visibility="gone" />

    </LinearLayout>
</ScrollView>
```

### Activity代码
```kotlin
package com.example.bookexchange.ui

import android.content.Context
import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.example.bookexchange.R
import com.example.bookexchange.model.Book
import com.example.bookexchange.viewmodel.BookViewModel
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import android.widget.ProgressBar

class AddBookActivity : AppCompatActivity() {
    private lateinit var bookViewModel: BookViewModel
    private lateinit var titleEditText: TextInputEditText
    private lateinit var authorEditText: TextInputEditText
    private lateinit var priceEditText: TextInputEditText
    private lateinit var descriptionEditText: TextInputEditText
    private lateinit var conditionAutoComplete: AutoCompleteTextView
    private lateinit var categoryAutoComplete: AutoCompleteTextView
    private lateinit var publishButton: MaterialButton
    private lateinit var progressBar: ProgressBar
    
    private var currentUserId: Long = -1
    private var currentUsername: String = ""
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_book)
        
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        title = "发布书籍"
        
        // Get user info
        val prefs = getSharedPreferences("BookExchange", Context.MODE_PRIVATE)
        currentUserId = prefs.getLong("userId", -1)
        currentUsername = prefs.getString("username", "") ?: ""
        
        initViews()
        setupViewModel()
        setupDropdowns()
        setupListeners()
    }
    
    private fun initViews() {
        titleEditText = findViewById(R.id.titleEditText)
        authorEditText = findViewById(R.id.authorEditText)
        priceEditText = findViewById(R.id.priceEditText)
        descriptionEditText = findViewById(R.id.descriptionEditText)
        conditionAutoComplete = findViewById(R.id.conditionAutoComplete)
        categoryAutoComplete = findViewById(R.id.categoryAutoComplete)
        publishButton = findViewById(R.id.publishButton)
        progressBar = findViewById(R.id.progressBar)
    }
    
    private fun setupViewModel() {
        bookViewModel = ViewModelProvider(this)[BookViewModel::class.java]
        
        bookViewModel.isLoading.observe(this) { isLoading ->
            progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
            publishButton.isEnabled = !isLoading
        }
        
        bookViewModel.toastMessage.observe(this) { message ->
            message?.let {
                Toast.makeText(this, it, Toast.LENGTH_SHORT).show()
                bookViewModel.clearEvents()
                if (it.contains("成功")) {
                    finish()
                }
            }
        }
    }
    
    private fun setupDropdowns() {
        val conditions = arrayOf("全新", "九成新", "八成新", "七成新")
        val conditionAdapter = ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, conditions)
        conditionAutoComplete.setAdapter(conditionAdapter)
        
        val categories = arrayOf("教材", "小说", "技术书籍", "其他")
        val categoryAdapter = ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, categories)
        categoryAutoComplete.setAdapter(categoryAdapter)
    }
    
    private fun setupListeners() {
        publishButton.setOnClickListener {
            publishBook()
        }
    }
    
    private fun publishBook() {
        val title = titleEditText.text.toString().trim()
        val author = authorEditText.text.toString().trim()
        val priceStr = priceEditText.text.toString().trim()
        val description = descriptionEditText.text.toString().trim()
        val condition = conditionAutoComplete.text.toString()
        val category = categoryAutoComplete.text.toString()
        
        // Validation
        if (title.isEmpty() || author.isEmpty() || priceStr.isEmpty() || 
            description.isEmpty() || condition.isEmpty() || category.isEmpty()) {
            Toast.makeText(this, "请填写所有必填字段", Toast.LENGTH_SHORT).show()
            return
        }
        
        val price = priceStr.toDoubleOrNull()
        if (price == null || price <= 0) {
            Toast.makeText(this, "请输入有效的价格", Toast.LENGTH_SHORT).show()
            return
        }
        
        val book = Book(
            title = title,
            author = author,
            price = price,
            description = description,
            condition = condition,
            category = category,
            images = "[]",  // Empty JSON array for now
            sellerId = currentUserId,
            sellerName = currentUsername
        )
        
        bookViewModel.addBook(book)
    }
    
    override fun onSupportNavigateUp(): Boolean {
        finish()
        return true
    }
}
```

---

## 4. BookDetailActivity实现

参考AddBookActivity的结构，创建详情展示页面：
- 显示书籍大图
- 显示详细信息
- 显示卖家信息
- 添加"联系卖家"按钮
- 添加"购买"按钮
- 添加收藏按钮

---

## 5. 图片上传功能

### 使用Android的图片选择器
```kotlin
// In AddBookActivity
private val imagePickerLauncher = registerForActivityResult(
    ActivityResultContracts.GetContent()
) { uri: Uri? ->
    uri?.let {
        // Handle selected image
        Glide.with(this)
            .load(uri)
            .into(bookImage1)
    }
}

// In setupListeners()
addImageButton.setOnClickListener {
    imagePickerLauncher.launch("image/*")
}
```

---

## 6. 交易功能实现

创建TransactionViewModel和相关UI，实现买卖双方的交易流程。

---

## 7. 消息功能实现

创建MessageViewModel和聊天UI，实现用户间的消息通信。

---

## 测试清单

- [ ] 用户注册流程
- [ ] 用户登录流程
- [ ] 邮箱验证流程（倒计时、频率限制）
- [ ] 发布书籍功能
- [ ] 搜索和筛选功能
- [ ] 收藏功能
- [ ] 书籍详情查看
- [ ] 交易流程
- [ ] 消息通信

---

## 常见问题

### Q: 如何测试验证码功能？
A: 验证码存储在数据库中，可以通过Android Studio的Database Inspector查看生成的验证码。

### Q: 图片存储在哪里？
A: 建议使用外部存储或云存储服务（如阿里云OSS、七牛云等）。

### Q: 如何实现实时消息？
A: 可以使用Firebase Cloud Messaging或者WebSocket实现。

---

完成以上功能后，BookExchange将成为一个功能完整的校园二手书交易平台！
