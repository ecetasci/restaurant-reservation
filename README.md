# Restaurant Rezervasyon Sistemi

Bu proje, restoranlar için masa rezervasyonları yönetmeye yönelik geliştirilmiş bir uygulamadır. Kullanıcılar, müşteriler ve yöneticiler masa durumlarını görüntüleyebilir, rezervasyon oluşturabilir ve yönetebilir.

## Özellikler

- **Admin Yönetimi**: Yönetici bilgileri oluşturma, alma ve şifre güncelleme.
- **Müşteri Yönetimi**: Müşteri bilgileri oluşturma ve görüntüleme.
- **Restoran Yönetimi**: Restoran bilgilerini görüntüleme.
- **Rezervasyon Yönetimi**: Rezervasyon oluşturma, görüntüleme ve silme.

## Kurulum

1. **Projeyi klonlayın:**

```bash
git clone https://github.com/ecetasci/restaurant-reservation.git
```

2. **Dizine girin:**

```bash
cd restaurant-reservation
```

3. **Bağımlılıkları yükleyin:**

```bash
./mvnw install
```

4. **Uygulamayı çalıştırın:**

```bash
./mvnw spring-boot:run
```

5. **Tarayıcıda görüntüleyin:**

```
http://localhost:8080
```

## API Uç Noktaları

### Admin İşlemleri (`/api/admin`)

- **Admin Bilgilerini Getirme**:
  - Endpoint: `GET /api/admin/{id}`
  - Açıklama: Belirtilen ID'ye sahip admin bilgilerini getirir.

- **Admin Kaydetme**:
  - Endpoint: `POST /api/admin/save`
  - Açıklama: Yeni admin oluşturur ve oluşturulan admin ID'sini döner.

- **Rezervasyonları Listeleme (Admin Girişi ile)**:
  - Endpoint: `POST /api/admin/reservations`
  - Açıklama: Admin giriş bilgileriyle (AdminDto) tüm rezervasyonları listeler.

- **Admin Şifre Değişikliği**:
  - Endpoint: `POST /api/admin/password`
  - Açıklama: Admin'in mevcut bilgileriyle şifresini günceller.

### Müşteri İşlemleri (`/api/customer`)

- **Müşteri Kaydetme**:
  - Endpoint: `POST /api/customer/save`
  - Açıklama: Yeni müşteri oluşturur ve oluşturulan müşteri ID'sini döner.

- **Müşteri Bilgilerini Getirme**:
  - Endpoint: `GET /api/customer/{id}`
  - Açıklama: Belirtilen ID'ye sahip müşteri bilgilerini getirir.

- **Rezervasyonları Listeleme (Müşteri Girişi ile)**:
  - Endpoint: `POST /api/customer/reservations`
  - Açıklama: Müşteri giriş bilgileriyle (CustomerDto) tüm rezervasyonları listeler.

### Rezervasyon İşlemleri (`/api/reservation`)

- **Rezervasyon Oluşturma**:
  - Endpoint: `POST /api/reservation/create`
  - Açıklama: Yeni rezervasyon oluşturur.

- **Rezervasyon Bilgilerini Getirme**:
  - Endpoint: `GET /api/reservation/{id}`
  - Açıklama: Belirtilen ID'ye sahip rezervasyonun detaylarını getirir.

- **Rezervasyon Listeleme (AdminDto ile, Deprecated)**:
  - Endpoint: `POST /api/reservation/list`
  - Açıklama: Belirli admin bilgileriyle tüm rezervasyonları listeler. (Deprecated)

- **Rezervasyon Kaydetme (Deprecated)**:
  - Endpoint: `POST /api/reservation/save`
  - Açıklama: Rezervasyonu direkt entity ile kaydeder. (Deprecated)

### Restoran İşlemleri (`/api/restaurant`)

- **Restoran Bilgilerini Getirme**:
  - Endpoint: `GET /api/restaurant/{id}`
  - Açıklama: Belirtilen ID'ye sahip restoran bilgilerini getirir.

- **Restoran Kaydetme (Deprecated)**:
  - Endpoint: `POST /api/restaurant/save`
  - Açıklama: Yeni restoran oluşturur. (Deprecated)

### Masa İşlemleri (`/tables`)

- **Masaları Listeleme**:
  - Endpoint: `GET /tables`
  - Açıklama: Restorandaki tüm masaları listeler.

- **Belirli Masa Detayı**:
  - Endpoint: `GET /tables/{id}`
  - Açıklama: Belirtilen ID'ye sahip masanın detaylarını getirir.

## Katkıda Bulunma

Katkıda bulunmak için:

- Repoyu fork edin.
- Değişikliklerinizi kendi fork ettiğiniz repoya yükleyin.
- Bir pull request oluşturun.

## Lisans

Bu proje MIT lisansı altında lisanslanmıştır. Ayrıntılar için `LICENSE` dosyasını inceleyebilirsiniz.
