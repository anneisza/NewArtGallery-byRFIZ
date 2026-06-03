-- SQL Initialization Script for Galeri Seni
-- Target Database: galeri_seni

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
START TRANSACTION;
SET time_zone = "+00:00";

-- --------------------------------------------------------
-- 1. Table structure for table `users`
-- --------------------------------------------------------
CREATE TABLE IF NOT EXISTS `users` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `name` varchar(100) NOT NULL,
  `email` varchar(100) NOT NULL,
  `password` varchar(255) NOT NULL,
  `role` enum('ADMIN','KURATOR') NOT NULL,
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `email` (`email`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- Dumping data for table `users`
INSERT IGNORE INTO `users` (`id`, `name`, `email`, `password`, `role`, `created_at`) VALUES
(1, 'Admin Utama', 'admin@artgallery.art', '$2y$10$VavyHHRN/JXSR33T53lR7eCTF/HHLbg6vAyjPkup5ipmbVDPK7sTK', 'ADMIN', '2026-05-30 16:24:24'),
(4, 'Rain Rosidi', 'kurator@artgallery.art', '$2y$10$K6b14kXuOBgKxtpazmAapu/gPUNnsdOoI7fgwL/k/TYu5fw07ayVa', 'KURATOR', '2026-05-30 16:24:24');

-- --------------------------------------------------------
-- 2. Table structure for table `artists`
-- --------------------------------------------------------
CREATE TABLE IF NOT EXISTS `artists` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `name` varchar(100) NOT NULL,
  `specialty` varchar(100) DEFAULT NULL,
  `bio` text,
  `photo` varchar(255) DEFAULT NULL,
  `total_works` int DEFAULT '0',
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- Dumping data for table `artists`
INSERT IGNORE INTO `artists` (`id`, `name`, `specialty`, `bio`, `photo`, `total_works`, `created_at`) VALUES
(9, 'Raden Saleh', 'Seni lukis beraliran Romantisisme Eropa.', 'Raden Saleh Syarif Bustaman (sekitar 1811–1880) adalah perintis seni lukis modern Indonesia. Berasal dari keluarga ningrat Jawa-Arab, ia menjadi pelukis istana di Eropa dan menggabungkan gaya romantisme Barat dengan elemen lokal. Karya legendarisnya yang paling ikonik adalah Penangkapan Pangeran Diponegoro.', '/assets/artists/1780482524803_Pelukis Jawa di Eropa.jfif', 0, '2026-06-03 09:06:51'),
(10, 'Leonardo da Vinci', 'Pelukis, pemahat, dan arsitek. Ia menguasai teknik sfumato', 'Leonardo da Vinci (1452–1519) adalah sosok jenius dari era Renaisans Italia. Ia adalah polimatik—seseorang yang menguasai berbagai disiplin ilmu—yang paling dikenal publik sebagai pelukis mahakarya Mona Lisa dan The Last Supper (Perjamuan Terakhir).', '/assets/artists/1780491087338_7.png', 0, '2026-06-03 10:35:08'),
(11, 'Mantra Ardhana', 'Seni Lukis Klasik, Kontemporer, Media Baru / AI', 'Mantra Ardhana (lahir di Cakranegara, Lombok, 22 Agustus 1971) adalah seniman visual dan perupa kontemporer lulusan Institut Seni Indonesia (ISI) Yogyakarta. Ia berspesialisasi dalam seni lintas medium, mengeksplorasi lukisan klasik, cat air (watercolor), seni rupa berbasis teknologi (Artificial Intelligence/AI), musik, hingga performance art', '/assets/artists/1780491104571_5.png', 0, '2026-06-03 10:40:15'),
(12, 'Refik Anadol', 'Seni AI dan Lukisan Data', 'Refik Anadol (lahir 1985, Istanbul) adalah seniman media dan pelopor seni Kecerdasan Buatan (AI) asal Turki-Amerika. Ia dikenal sebagai direktur Refik Anadol Studio di Los Angeles dan pengajar di UCLA. Karya Anadol berada di persimpangan antara seni, arsitektur, sains, dan komputasi', '/assets/artists/1780491125219_3.png', 0, '2026-06-03 10:44:15');

-- --------------------------------------------------------
-- 3. Table structure for table `exhibitions`
-- --------------------------------------------------------
CREATE TABLE IF NOT EXISTS `exhibitions` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `title` varchar(200) NOT NULL,
  `description` text,
  `location` varchar(200) DEFAULT NULL,
  `start_date` date DEFAULT NULL,
  `end_date` date DEFAULT NULL,
  `status` enum('ONGOING','UPCOMING','PAST') DEFAULT 'UPCOMING',
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- Dumping data for table `exhibitions`
INSERT IGNORE INTO `exhibitions` (`id`, `title`, `description`, `location`, `start_date`, `end_date`, `status`, `created_at`) VALUES
(5, 'Museum of Modern Art (MoMA)', '', 'New York', '2022-01-01', '2022-12-31', 'PAST', '2026-06-03 11:01:34');

-- --------------------------------------------------------
-- 4. Table structure for table `artworks`
-- --------------------------------------------------------
CREATE TABLE IF NOT EXISTS `artworks` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `title` varchar(200) NOT NULL,
  `artist_id` bigint DEFAULT NULL,
  `exhibition_id` bigint DEFAULT NULL,
  `image_url` varchar(255) DEFAULT NULL,
  `description` text,
  `year_created` int DEFAULT NULL,
  `curation_status` enum('PENDING','APPROVED','REJECTED') DEFAULT 'PENDING',
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `artist_id` (`artist_id`),
  KEY `exhibition_id` (`exhibition_id`),
  CONSTRAINT `artworks_ibfk_1` FOREIGN KEY (`artist_id`) REFERENCES `artists` (`id`) ON DELETE SET NULL,
  CONSTRAINT `artworks_ibfk_2` FOREIGN KEY (`exhibition_id`) REFERENCES `exhibitions` (`id`) ON DELETE SET NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- Dumping data for table `artworks`
INSERT IGNORE INTO `artworks` (`id`, `title`, `artist_id`, `exhibition_id`, `image_url`, `description`, `year_created`, `curation_status`, `created_at`) VALUES
(8, 'Penangkapan Pangeran Diponegoro', 9, NULL, '/assets/artworks/1780491061326_6.png', ' Mahakarya ini menampilkan kritik politik dan nasionalisme Raden Saleh. Ia melukis adegan pengkhianatan Belanda terhadap Pangeran Diponegoro. Berbeda dengan versi pelukis Belanda (Nicolaas Pieneman), Raden Saleh menggambarkan wajah Pangeran Diponegoro dengan ekspresi marah dan menahan emosi, memberikan simbol perlawanan terhadap kolonialisme.', 1857, 'APPROVED', '2026-06-03 10:51:52'),
(9, 'Mona Lisa', 10, NULL, '/assets/artworks/1780491041193_4.png', ' Salah satu lukisan paling ikonik di dunia yang menggambarkan Lisa Gherardini. Menggunakan teknik inovatif sfumato (gradasi warna yang membaur dan mengaburkan garis) yang menghasilkan kesan berkabut dan ekspresi senyuman misterius.', 1503, 'APPROVED', '2026-06-03 10:53:57'),
(10, 'The Brayut', 11, NULL, '/assets/artworks/1780491022229_2.png', ' Dalam pameran ini, Mantra menyuguhkan 27 karya visual yang memadukan teknologi AI dan media kanvas. Ia menggunakan AI untuk mendeskripsikan bentuk, warna, dan filosofi visual, menjadikan AI sebagai bahan mentah yang ia latih sendiri (menggunakan private model). Karya-karya ini seringkali terinspirasi dari filosofi tradisi dan cerita lokal (seperti cerita Men Brayut), sekaligus merenungkan dampak teknologi agar manusia tidak kehilangan keintiman dan spiritualitas di era digita', 2023, 'APPROVED', '2026-06-03 10:57:13'),
(11, 'Unsupervised', 12, NULL, '/assets/artworks/1780491001854_1.png', ' Karya ini menggunakan model AI generatif tingkat lanjut untuk memproses, menganalisis, dan \"berhalusinasi\" tentang 200 tahun koleksi karya seni MoMA. Visual yang dihasilkan terus berubah dan tidak pernah berulang, memberikan interpretasi baru terhadap sejarah seni.', 2022, 'APPROVED', '2026-06-03 10:59:21');

-- --------------------------------------------------------
-- 5. Table structure for table `ai_attributions`
-- --------------------------------------------------------
CREATE TABLE IF NOT EXISTS `ai_attributions` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `artwork_id` bigint NOT NULL,
  `involvement_level` enum('PURE_HUMAN','AI_ASSISTED','FULLY_AI_GENERATED') NOT NULL,
  `software_used` varchar(200) DEFAULT NULL,
  `prompt_text` text,
  `recorded_by` bigint DEFAULT NULL,
  `recorded_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `artwork_id` (`artwork_id`),
  KEY `recorded_by` (`recorded_by`),
  CONSTRAINT `ai_attributions_ibfk_1` FOREIGN KEY (`artwork_id`) REFERENCES `artworks` (`id`) ON DELETE CASCADE,
  CONSTRAINT `ai_attributions_ibfk_2` FOREIGN KEY (`recorded_by`) REFERENCES `users` (`id`) ON DELETE SET NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- Dumping data for table `ai_attributions`
INSERT IGNORE INTO `ai_attributions` (`id`, `artwork_id`, `involvement_level`, `software_used`, `prompt_text`, `recorded_by`, `recorded_at`) VALUES
(6, 8, 'PURE_HUMAN', '', '', 1, '2026-06-03 11:06:31'),
(7, 9, 'PURE_HUMAN', '', '', 1, '2026-06-03 11:07:11'),
(8, 10, 'AI_ASSISTED', '', '', 1, '2026-06-03 11:07:27'),
(9, 11, 'AI_ASSISTED', '', '', 1, '2026-06-03 11:07:35');

-- --------------------------------------------------------
-- 6. Table structure for table `curations`
-- --------------------------------------------------------
CREATE TABLE IF NOT EXISTS `curations` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `artwork_id` bigint NOT NULL,
  `curator_id` bigint NOT NULL,
  `notes` text,
  `status` enum('PENDING','APPROVED','REJECTED') DEFAULT 'PENDING',
  `curated_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `artwork_id` (`artwork_id`),
  KEY `curator_id` (`curator_id`),
  CONSTRAINT `curations_ibfk_1` FOREIGN KEY (`artwork_id`) REFERENCES `artworks` (`id`) ON DELETE CASCADE,
  CONSTRAINT `curations_ibfk_2` FOREIGN KEY (`curator_id`) REFERENCES `users` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- Dumping data for table `curations`
INSERT IGNORE INTO `curations` (`id`, `artwork_id`, `curator_id`, `notes`, `status`, `curated_at`) VALUES
(10, 8, 1, '', 'APPROVED', '2026-06-03 11:03:14'),
(11, 9, 1, '', 'APPROVED', '2026-06-03 11:03:23'),
(12, 10, 1, '', 'APPROVED', '2026-06-03 11:03:38'),
(13, 11, 4, 'Bantuan AI\r\n', 'APPROVED', '2026-06-03 13:42:52');

-- --------------------------------------------------------
-- 7. Table structure for table `gallery_info`
-- --------------------------------------------------------
CREATE TABLE IF NOT EXISTS `gallery_info` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `gallery_name` varchar(200) DEFAULT 'ART GALLERY',
  `tagline` varchar(300) DEFAULT NULL,
  `description` text,
  `address` varchar(300) DEFAULT NULL,
  `opening_hours` varchar(200) DEFAULT NULL,
  `contact_email` varchar(100) DEFAULT NULL,
  `founded_year` int DEFAULT NULL,
  `updated_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- Dumping data for table `gallery_info`
INSERT IGNORE INTO `gallery_info` (`id`, `gallery_name`, `tagline`, `description`, `address`, `opening_hours`, `contact_email`, `founded_year`, `updated_at`) VALUES
(1, 'ART GALLERY', 'Discover the timeless beauty of ancient civilizations through a lens of modern preservation and digital excellence.', 'Didirikan pada 2018, Art Gallery adalah galeri digital terkemuka yang didedikasikan untuk menjembatani seni klasik dan komputasional. Kami believe pada transparansi radikal — setiap karya memiliki catatan atribusi yang jelas, termasuk peran AI di dalamnya.', 'Jl. Seni No. 12, Yogyakarta', 'Tue–Sun, 10:00–20:00 WIB', 'gallery@artgallery.art', 2018, '2026-06-01 03:09:19');

COMMIT;