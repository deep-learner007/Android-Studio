# 项目架构图 (Architecture Diagram)

## 整体架构 (Overall Architecture)

```
┌─────────────────────────────────────────────────────────────┐
│                        UI Layer (View)                       │
│  ┌──────────────┐  ┌──────────────┐  ┌──────────────┐      │
│  │   Activity   │  │   Fragment   │  │    Adapter   │      │
│  │              │  │              │  │              │      │
│  │ • Login      │  │ • BookList   │  │ • Book       │      │
│  │ • Register   │  │ • Favorites  │  │ • Message    │      │
│  │ • Main       │  │ • Messages   │  │ • Transaction│      │
│  │ • AddBook    │  │ • Profile    │  │              │      │
│  │ • BookDetail │  │ • CampusVerify│ │              │      │
│  └──────┬───────┘  └──────┬───────┘  └──────┬───────┘      │
└─────────┼──────────────────┼──────────────────┼─────────────┘
          │                  │                  │
          │    LiveData      │    LiveData      │
          │    Observe       │    Observe       │
          ▼                  ▼                  ▼
┌─────────────────────────────────────────────────────────────┐
│                    ViewModel Layer                           │
│  ┌────────────────────────────────────────────────────────┐ │
│  │              AuthViewModel                              │ │
│  │  • toastMessage: LiveData<String>                      │ │
│  │  • verifySuccess: LiveData<Boolean>                    │ │
│  │  • sendingCode: LiveData<Boolean>  ◄─ Loading State   │ │
│  │  • verifyingCode: LiveData<Boolean> ◄─ Loading State  │ │
│  │  • resendCountdown: LiveData<Int>  ◄─ Countdown       │ │
│  │  • loginSuccess: LiveData<User>                        │ │
│  │  • registerSuccess: LiveData<User>                     │ │
│  │                                                         │ │
│  │  + sendSchoolEmailCode()     ◄─ Anti-duplicate        │ │
│  │  + verifySchoolEmailCode()   ◄─ Validation            │ │
│  │  + register()                ◄─ Input check           │ │
│  │  + login()                   ◄─ Session               │ │
│  │  + clearEvents()             ◄─ Event clearing        │ │
│  └────────────────────────────────────────────────────────┘ │
│                                                              │
│  ┌────────────────────────────────────────────────────────┐ │
│  │              BookViewModel                              │ │
│  │  • allBooks: LiveData<List<Book>>                      │ │
│  │  • isLoading: LiveData<Boolean>                        │ │
│  │  • toastMessage: LiveData<String>                      │ │
│  │                                                         │ │
│  │  + searchBooks(query)                                  │ │
│  │  + getBooksByCategory(category)                        │ │
│  │  + toggleFavorite(userId, bookId)                      │ │
│  │  + addBook(book)                                       │ │
│  └────────────────────────────────────────────────────────┘ │
└──────────────────────┬───────────────────────────────────────┘
                       │
                       │ Uses
                       ▼
┌─────────────────────────────────────────────────────────────┐
│                   Repository Layer                           │
│  ┌──────────────────────┐    ┌──────────────────────┐      │
│  │   AuthRepository     │    │   BookRepository     │      │
│  │                      │    │                      │      │
│  │  + register()        │    │  + getAllBooks()     │      │
│  │  + login()           │    │  + searchBooks()     │      │
│  │  + sendEmailCode()   │    │  + addBook()         │      │
│  │    ◄ Rate Limit 3/h  │    │  + toggleFavorite()  │      │
│  │    ◄ Code expire 10m │    │  + updateStatus()    │      │
│  │  + verifyEmailCode() │    │                      │      │
│  │  + hashPassword()    │    │                      │      │
│  │    ◄ SHA-256         │    │                      │      │
│  └──────────┬───────────┘    └──────────┬───────────┘      │
└─────────────┼────────────────────────────┼──────────────────┘
              │                            │
              │ Uses                       │ Uses
              ▼                            ▼
┌─────────────────────────────────────────────────────────────┐
│                     Database Layer (Room)                    │
│  ┌────────────────────────────────────────────────────────┐ │
│  │                     AppDatabase                         │ │
│  │  ┌──────────────┐  ┌──────────────┐  ┌──────────────┐ │ │
│  │  │   UserDao    │  │   BookDao    │  │FavoriteDao   │ │ │
│  │  │              │  │              │  │              │ │ │
│  │  │ • insert()   │  │ • insert()   │  │ • insert()   │ │ │
│  │  │ • update()   │  │ • update()   │  │ • delete()   │ │ │
│  │  │ • getUserBy  │  │ • search()   │  │ • getFav()   │ │ │
│  │  │   Email()    │  │ • filter()   │  │              │ │ │
│  │  │ • login()    │  │ • getBy      │  │              │ │ │
│  │  │ • markEmail  │  │   Category() │  │              │ │ │
│  │  │   Verified() │  │              │  │              │ │ │
│  │  └──────────────┘  └──────────────┘  └──────────────┘ │ │
│  │                                                         │ │
│  │  ┌──────────────┐  ┌──────────────┐  ┌──────────────┐ │ │
│  │  │ MessageDao   │  │Transaction   │  │Verification  │ │ │
│  │  │              │  │    Dao       │  │  CodeDao     │ │ │
│  │  │ • getConv()  │  │              │  │              │ │ │
│  │  │ • markRead() │  │ • getAsB     │  │ • getValid   │ │ │
│  │  │ • unreadCnt()│  │   uyer()     │  │   Code()     │ │ │
│  │  │              │  │ • getAsS     │  │ • markUsed() │ │ │
│  │  │              │  │   eller()    │  │ • deleteExp()│ │ │
│  │  │              │  │ • update     │  │ • getCodes   │ │ │
│  │  │              │  │   Status()   │  │   SentCnt()  │ │ │
│  │  └──────────────┘  └──────────────┘  └──────────────┘ │ │
│  └────────────────────────────────────────────────────────┘ │
└──────────────────────┬───────────────────────────────────────┘
                       │
                       │ Stores to
                       ▼
┌─────────────────────────────────────────────────────────────┐
│                     Data Model Layer                         │
│  ┌──────────┐  ┌──────────┐  ┌──────────┐  ┌──────────┐   │
│  │   User   │  │   Book   │  │Transaction│ │ Favorite │   │
│  │          │  │          │  │          │  │          │   │
│  │ • id     │  │ • id     │  │ • id     │  │ • userId │   │
│  │ • email  │  │ • title  │  │ • bookId │  │ • bookId │   │
│  │ • pwd    │  │ • author │  │ • buyerId│  │ • created│   │
│  │ • school │  │ • price  │  │ • sellerId│ │   At     │   │
│  │   Email  │  │ • cond   │  │ • status │  │          │   │
│  │ • credit │  │ • categ  │  │ • price  │  │          │   │
│  │ • banned │  │ • images │  │ • message│  │          │   │
│  └──────────┘  └──────────┘  └──────────┘  └──────────┘   │
│                                                              │
│  ┌──────────┐  ┌──────────────────────┐                    │
│  │ Message  │  │  VerificationCode    │                    │
│  │          │  │                      │                    │
│  │ • sender │  │ • userId             │                    │
│  │ • receiver│ │ • email              │                    │
│  │ • content│  │ • code (6 digits)    │                    │
│  │ • bookId │  │ • createdAt          │                    │
│  │ • isRead │  │ • expiresAt (10min)  │                    │
│  └──────────┘  │ • isUsed             │                    │
│                └──────────────────────┘                    │
└─────────────────────────────────────────────────────────────┘
```

## 数据流向 (Data Flow)

### 1. 用户登录流程 (Login Flow)
```
┌──────────────┐       ┌──────────────┐       ┌──────────────┐       ┌──────────┐
│ LoginActivity│──────▶│AuthViewModel │──────▶│AuthRepository│──────▶│  UserDao │
│              │       │              │       │              │       │          │
│ User clicks  │       │ Validate     │       │ Hash pwd     │       │ Query DB │
│ login button │       │ input        │       │ SHA-256      │       │          │
│              │◀──────│              │◀──────│              │◀──────│          │
│ Show success │       │ loginSuccess │       │ Return user  │       │ User obj │
│ Navigate to  │       │ LiveData     │       │              │       │          │
│ MainActivity │       │              │       │              │       │          │
└──────────────┘       └──────────────┘       └──────────────┘       └──────────┘
```

### 2. 发送验证码流程 (Send Verification Code Flow)
```
┌──────────────────┐       ┌──────────────┐       ┌──────────────┐
│CampusVerifyFragment│       │AuthViewModel │       │AuthRepository│
│                  │       │              │       │              │
│ User enters      │       │              │       │              │
│ school email     │       │              │       │              │
│                  │       │              │       │              │
│ Clicks "Send"    │──────▶│ Check state  │       │              │
│                  │       │ sendingCode? │       │              │
│                  │       │ countdown > 0?│      │              │
│                  │       │              │       │              │
│                  │       │ If OK:       │       │              │
│                  │       │ sendingCode  │       │              │
│                  │       │   = true     │──────▶│ Check rate   │
│                  │       │              │       │ limit (3/h)  │
│                  │       │              │       │              │
│                  │       │              │       │ Generate code│
│                  │       │              │       │ (6 digits)   │
│                  │       │              │       │              │
│                  │       │              │       │ Save to DB   │
│                  │       │              │       │ expiresAt +10m│
│                  │◀──────│ sendingCode  │◀──────│              │
│ Button enabled   │       │   = false    │       │ Success      │
│ Start countdown  │       │ countdown(60)│       │              │
│ 60...59...58...  │       │              │       │              │
└──────────────────┘       └──────────────┘       └──────────────┘
```

### 3. 搜索书籍流程 (Search Books Flow)
```
┌──────────────┐       ┌──────────────┐       ┌──────────────┐       ┌──────────┐
│BookListFrag  │       │BookViewModel │       │BookRepository│       │ BookDao  │
│              │       │              │       │              │       │          │
│ User types   │       │              │       │              │       │          │
│ in search box│──────▶│ searchBooks()│──────▶│ searchBooks()│──────▶│ SELECT * │
│              │       │              │       │              │       │ WHERE... │
│              │       │              │       │              │       │ LIKE '%q%│
│              │◀──────│ LiveData     │◀──────│ LiveData     │◀──────│          │
│ Adapter      │       │ <List<Book>> │       │ <List<Book>> │       │ Results  │
│ updates list │       │              │       │              │       │          │
└──────────────┘       └──────────────┘       └──────────────┘       └──────────┘
```

## 状态管理示例 (State Management Example)

### AuthViewModel 状态图
```
                     Initial State
                          │
                          │
                          ▼
           ┌──────────────────────────┐
           │  All LiveData = false    │
           │  sendingCode = false     │
           │  verifyingCode = false   │
           │  isLoading = false       │
           │  resendCountdown = 0     │
           └──────────────────────────┘
                          │
        ┌─────────────────┼─────────────────┐
        │                 │                 │
        ▼                 ▼                 ▼
  ┌─────────┐      ┌─────────┐      ┌─────────┐
  │ SENDING │      │VERIFYING│      │ LOADING │
  │  CODE   │      │  CODE   │      │ (Login/ │
  │         │      │         │      │Register)│
  │ Button  │      │ Button  │      │         │
  │disabled │      │disabled │      │ Button  │
  │         │      │         │      │disabled │
  │Progress │      │Progress │      │         │
  │visible  │      │visible  │      │Progress │
  │         │      │         │      │visible  │
  │Countdown│      │         │      │         │
  │starts   │      │         │      │         │
  └────┬────┘      └────┬────┘      └────┬────┘
       │                │                │
       │ Success/Error  │ Success/Error  │ Success/Error
       │                │                │
       ▼                ▼                ▼
  ┌─────────────────────────────────────┐
  │     Toast Message + Clear Events    │
  │                                     │
  │  • Show message                     │
  │  • Reset LiveData to null           │
  │  • Re-enable buttons                │
  │  • Hide progress                    │
  └─────────────────────────────────────┘
```

## 安全机制 (Security Mechanisms)

```
┌────────────────────────────────────────────────────────┐
│                  Security Layers                        │
├────────────────────────────────────────────────────────┤
│                                                         │
│  Layer 1: Input Validation (Frontend)                  │
│  ┌─────────────────────────────────────────────────┐  │
│  │ • Email format regex check                       │  │
│  │ • Password length >= 6                           │  │
│  │ • Username length >= 3                           │  │
│  │ • No empty fields                                │  │
│  │ • Confirmation password match                    │  │
│  │ • Verification code 6 digits only                │  │
│  └─────────────────────────────────────────────────┘  │
│                      │                                  │
│                      ▼                                  │
│  Layer 2: State Management                             │
│  ┌─────────────────────────────────────────────────┐  │
│  │ • Prevent duplicate button clicks                │  │
│  │ • Loading states (sendingCode, verifyingCode)    │  │
│  │ • Countdown timer (60 seconds)                   │  │
│  │ • Button disabling during operations             │  │
│  └─────────────────────────────────────────────────┘  │
│                      │                                  │
│                      ▼                                  │
│  Layer 3: Rate Limiting (Backend)                      │
│  ┌─────────────────────────────────────────────────┐  │
│  │ • Max 3 verification codes per hour              │  │
│  │ • Check database for recent codes                │  │
│  │ • Reject if limit exceeded                       │  │
│  └─────────────────────────────────────────────────┘  │
│                      │                                  │
│                      ▼                                  │
│  Layer 4: Code Expiration                              │
│  ┌─────────────────────────────────────────────────┐  │
│  │ • Verification codes expire in 10 minutes        │  │
│  │ • Check expiresAt timestamp                      │  │
│  │ • Auto-delete expired codes                      │  │
│  └─────────────────────────────────────────────────┘  │
│                      │                                  │
│                      ▼                                  │
│  Layer 5: Password Encryption                          │
│  ┌─────────────────────────────────────────────────┐  │
│  │ • SHA-256 hashing                                │  │
│  │ • Never store plain text passwords              │  │
│  │ • One-way encryption                             │  │
│  └─────────────────────────────────────────────────┘  │
│                      │                                  │
│                      ▼                                  │
│  Layer 6: Database Security                            │
│  ┌─────────────────────────────────────────────────┐  │
│  │ • Room prevents SQL injection automatically      │  │
│  │ • Parameterized queries                          │  │
│  │ • Type-safe database access                      │  │
│  └─────────────────────────────────────────────────┘  │
│                                                         │
└────────────────────────────────────────────────────────┘
```

## UI组件层次 (UI Component Hierarchy)

```
MainActivity
├── Toolbar
│   └── Title (changes per tab)
├── BottomNavigationView (4 tabs)
│   ├── Home (BookListFragment)
│   ├── Favorites (FavoritesFragment)
│   ├── Messages (MessagesFragment)
│   └── Profile (ProfileFragment)
├── FragmentContainer
│   └── [Current Fragment]
│       └── BookListFragment
│           ├── SearchBar
│           │   └── TextInputEditText
│           ├── FilterButton
│           └── RecyclerView
│               └── BookAdapter
│                   └── item_book.xml (Card)
│                       ├── ImageView (book cover)
│                       ├── Title TextView
│                       ├── Author TextView
│                       ├── Price TextView
│                       ├── Condition TextView
│                       └── Favorite ImageView
└── FloatingActionButton (Add Book)
    └── onClick → AddBookActivity
```

## 总结 (Summary)

本项目实现了完整的MVVM架构，包含：

- ✅ **6个数据模型** (User, Book, Transaction, Favorite, VerificationCode, Message)
- ✅ **6个DAO接口** (完整的CRUD操作)
- ✅ **2个Repository** (数据层抽象)
- ✅ **1个完整的ViewModel** (AuthViewModel with 所有状态管理)
- ✅ **6个Activity** (完整的UI流程)
- ✅ **5个Fragment** (模块化UI设计)
- ✅ **多层安全机制** (验证、加密、限流、过期)
- ✅ **Material Design 3** (现代化UI设计)
- ✅ **完整的文档** (README, UPGRADE_SUMMARY, IMPLEMENTATION_GUIDE)

**可以直接开始实现Adapter和连接ViewModel与UI！** 🚀
