# Quản lý Ngữ pháp - JChiiki Admin

## Tổng quan
Phần quản lý ngữ pháp cho phép admin thêm, sửa, xóa các bài học ngữ pháp và điểm ngữ pháp trong ứng dụng JChiiki.

## Cấu trúc dữ liệu

### Bài học ngữ pháp (Grammar Lessons)
- **LessonID**: ID duy nhất của bài học
- **LessonTitle**: Tiêu đề bài học (VD: "Bài 1")
- **LessonName**: Tên bài học
- **LessonImage**: URL hình ảnh bài học

### Điểm ngữ pháp (Grammar Points)
- **GrammarPointID**: ID duy nhất của điểm ngữ pháp
- **GrammarPoint**: Tên điểm ngữ pháp (VD: "は particle")
- **Explanation**: Giải thích ngữ pháp
- **Example**: Ví dụ sử dụng
- **Translation**: Bản dịch ví dụ

## Chức năng chính

### 1. Quản lý bài học ngữ pháp
- **Thêm bài học mới**: Nhấn "Thêm bài học ngữ pháp"
- **Xem danh sách**: Hiển thị tất cả bài học ngữ pháp
- **Sửa bài học**: Nhấn nút "Sửa" trên từng bài học
- **Xóa bài học**: Nhấn nút "Xóa" trên từng bài học

### 2. Quản lý điểm ngữ pháp
- **Tìm kiếm bài học**: Nhập tiêu đề bài học và nhấn "Tìm kiếm"
- **Thêm điểm ngữ pháp**: Nhấn "Thêm điểm ngữ pháp"
- **Xem danh sách**: Hiển thị tất cả điểm ngữ pháp trong bài học
- **Sửa điểm ngữ pháp**: Nhấn nút "Sửa" trên từng điểm
- **Xóa điểm ngữ pháp**: Nhấn nút "Xóa" trên từng điểm

## Cách sử dụng

### Thêm bài học ngữ pháp mới
1. Nhấn "Thêm bài học ngữ pháp"
2. Nhập tiêu đề theo định dạng "Bài <số>" (VD: "Bài 1")
3. Nhập tên bài học
4. Nhập URL hình ảnh bài học
5. Nhấn "Thêm"

### Thêm điểm ngữ pháp
1. Nhập tiêu đề bài học vào ô tìm kiếm
2. Nhấn "Tìm kiếm"
3. Nhấn "Thêm điểm ngữ pháp"
4. Nhập thông tin điểm ngữ pháp:
   - Điểm ngữ pháp (VD: "は particle")
   - Giải thích ngữ pháp
   - Ví dụ sử dụng
   - Bản dịch ví dụ
5. Nhấn "Thêm"

## Cấu trúc Firebase

### Collection: "Grammars"
- **Document: "TOTAL_GRAMMARS"**
  - COUNT: Số lượng bài học
  - Lesson1_ID, Lesson2_ID, ...

- **Document: "Bài 1" (VD)**
  - Lesson1_ID: "Bài 1"
  - Lesson1_Name: "Tên bài học"
  - Lesson1_Title: "Bài 1"
  - Lesson1_Image: "URL hình ảnh"

### Subcollection: "GrammarPoints"
- **Document: "TOTAL_GRAMMAR_POINTS"**
  - COUNT: Số lượng điểm ngữ pháp

- **Document: "GrammarPoint_1" (VD)**
  - GrammarPoint1_ID: "GrammarPoint_1"
  - GrammarPoint1_Point: "は particle"
  - GrammarPoint1_Explanation: "Giải thích..."
  - GrammarPoint1_Example: "Ví dụ..."
  - GrammarPoint1_Translation: "Bản dịch..."

## Lưu ý
- Tiêu đề bài học phải theo định dạng "Bài <số>"
- Tất cả thông tin bắt buộc phải được nhập đầy đủ
- Dữ liệu được lưu trữ trong Firebase Firestore
- Có thể xem và quản lý dữ liệu trực tiếp trên Firebase Console

## File đã tạo
- `Admin_GrammarFragment.java`: Fragment chính quản lý ngữ pháp
- `GrammarLessons_AdminAdaptor.java`: Adapter cho danh sách bài học
- `GrammarContent_AdminAdaptor.java`: Adapter cho nội dung ngữ pháp
- `AdminGrammarPoint.java`: Model cho điểm ngữ pháp
- `admin_fragment_grammar.xml`: Layout fragment chính
- `admin_grammar_lesson_item.xml`: Layout item bài học
- `admin_grammar_content_item.xml`: Layout item điểm ngữ pháp
- `add_grammar_lesson_dialog.xml`: Dialog thêm bài học
- `add_grammar_point_dialog.xml`: Dialog thêm điểm ngữ pháp 