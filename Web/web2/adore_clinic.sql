-- phpMyAdmin SQL Dump
-- version 5.1.2
-- https://www.phpmyadmin.net/
--
-- Host: localhost:3306
-- Generation Time: Apr 15, 2025 at 05:50 PM
-- Server version: 5.7.24
-- PHP Version: 8.3.1

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
START TRANSACTION;
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;

--
-- Database: `adore_clinic`
--

-- --------------------------------------------------------

--
-- Table structure for table `appointment`
--

CREATE TABLE `appointment` (
  `id` int(11) NOT NULL,
  `PatientID` varchar(36) NOT NULL,
  `DoctorID` varchar(36) NOT NULL,
  `date` date NOT NULL,
  `time` time NOT NULL,
  `reason` text NOT NULL,
  `status` enum('Pending','Confirmed','Done') NOT NULL DEFAULT 'Pending'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

--
-- Dumping data for table `appointment`
--

INSERT INTO `appointment` (`id`, `PatientID`, `DoctorID`, `date`, `time`, `reason`, `status`) VALUES
(3, '456789', '123456', '2025-04-15', '04:30:00', 'check', 'Confirmed'),
(5, '345678', '678910', '2025-04-28', '15:00:00', 'check', 'Done'),
(6, '345678', '111213', '2025-04-06', '07:15:00', 'check', 'Done'),
(8, '456789', '123456', '2025-05-01', '15:15:00', 'check', 'Pending'),
(13, '345678', '123456', '2025-03-18', '04:00:00', 'check', 'Done'),
(14, '234567', '678910', '2025-03-02', '15:00:00', 'check', 'Pending'),
(16, '234567', '111213', '2025-04-16', '16:36:00', 'check', 'Pending'),
(19, '345678', '678910', '2025-04-30', '13:30:00', 'check', 'Pending');

-- --------------------------------------------------------

--
-- Table structure for table `doctor`
--

CREATE TABLE `doctor` (
  `id` varchar(36) NOT NULL,
  `firstName` varchar(50) NOT NULL,
  `lastName` varchar(50) NOT NULL,
  `uniqueFileName` varchar(255) NOT NULL,
  `SpecialityID` varchar(36) NOT NULL,
  `emailAddress` varchar(100) NOT NULL,
  `password` varchar(255) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

--
-- Dumping data for table `doctor`
--

INSERT INTO `doctor` (`id`, `firstName`, `lastName`, `uniqueFileName`, `SpecialityID`, `emailAddress`, `password`) VALUES
('111213', 'majed', 'Alnasser', 'doctor_67f0dfdd11a1b1.68297776.jpg', '4fba68ab-04ac-11f0-b246-ac198ed170b7', 'majed_Alnasser@gmail.com', '$2y$10$vUmT3e89Hrff1d6M.IJr2ud/3LqvoSL8zy8Oh.eOSg/lM02QVxCey'),
('123456', 'mohammed', 'alahmeday', 'doctor_67f0dec2e387e5.68626032.jpg', '1fa1dcbe-04ac-11f0-b246-ac198ed170b7', 'mohammed_ahmeday@gmail.com', '$2y$10$xIX.zfDXR61Xz0amqOK9y.afljiXd2UV0LmQXzX72AemQREl9jGO6'),
('678910', 'Sara', 'Alotaibi', 'doctor_67f0df6c8095d0.91996436.jpg', '4fb6becf-04ac-11f0-b246-ac198ed170b7', 'sara_Alotaibi@gmail.com', '$2y$10$g.jjF3vjePtbhFsly0ZlhexMmSD7zo8RbFeTwtvmGPGtv0lOU1.xO');

-- --------------------------------------------------------

--
-- Table structure for table `medication`
--

CREATE TABLE `medication` (
  `id` varchar(36) NOT NULL,
  `MedicationName` varchar(100) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

--
-- Dumping data for table `medication`
--

INSERT INTO `medication` (`id`, `MedicationName`) VALUES
('9743b14f-0ad2-11f0-a09d-106838549bd5', 'Hydrocortisone'),
('9743e457-0ad2-11f0-a09d-106838549bd5', 'Triamcinolone'),
('9744535d-0ad2-11f0-a09d-106838549bd5', 'Calcipotriene'),
('f69c1f22-04a5-11f0-b246-ac198ed170b7', 'Lipitor');

-- --------------------------------------------------------

--
-- Table structure for table `patient`
--

CREATE TABLE `patient` (
  `id` varchar(36) NOT NULL,
  `firstName` varchar(50) NOT NULL,
  `lastName` varchar(50) NOT NULL,
  `Gender` enum('Male','Female') NOT NULL,
  `DoB` date NOT NULL,
  `emailAddress` varchar(100) NOT NULL,
  `password` varchar(255) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

--
-- Dumping data for table `patient`
--

INSERT INTO `patient` (`id`, `firstName`, `lastName`, `Gender`, `DoB`, `emailAddress`, `password`) VALUES
('234567', 'nada', 'ahmed', 'Female', '2004-01-05', 'nada@gmail.com', '$2y$10$bG546bHq/R3pVJ7loNOQt.RsFLrpEy3kbYKCXi/dEHWQZ2qmbE6YK'),
('345678', 'leen', 'omer', 'Female', '2000-04-04', 'leen@gmail.com', '$2y$10$aZW8jC9ptwmEAyBmbqFbOu5XwM.l2WjDHX6Wi8k1Wqb62XQcfO756'),
('456789', 'jood', 'Alshahri', 'Female', '2006-05-15', 'jood@gmail.com', '$2y$10$vP.pOPG1Te/tthx76UfYRuYjix2igT5lNCSTE7HPjqRTdP4O8Mnj2');

-- --------------------------------------------------------

--
-- Table structure for table `prescription`
--

CREATE TABLE `prescription` (
  `id` varchar(36) NOT NULL,
  `AppointmentID` int(11) NOT NULL,
  `MedicationID` varchar(36) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

--
-- Dumping data for table `prescription`
--

INSERT INTO `prescription` (`id`, `AppointmentID`, `MedicationID`) VALUES
('67f1a64e6a2c6', 6, '9743b14f-0ad2-11f0-a09d-106838549bd5'),
('67f1a64e6c8cf', 6, '9744535d-0ad2-11f0-a09d-106838549bd5'),
('67f1a712d6042', 13, '9743b14f-0ad2-11f0-a09d-106838549bd5'),
('67f1a712d679e', 13, '9743e457-0ad2-11f0-a09d-106838549bd5'),
('67f1a712d6d12', 13, 'f69c1f22-04a5-11f0-b246-ac198ed170b7'),
('67fe29a852f2f', 5, '9743b14f-0ad2-11f0-a09d-106838549bd5'),
('67fe29a8541f0', 5, 'f69c1f22-04a5-11f0-b246-ac198ed170b7');

-- --------------------------------------------------------

--
-- Table structure for table `speciality`
--

CREATE TABLE `speciality` (
  `id` varchar(36) NOT NULL,
  `speciality` varchar(100) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

--
-- Dumping data for table `speciality`
--

INSERT INTO `speciality` (`id`, `speciality`) VALUES
('1fa1dcbe-04ac-11f0-b246-ac198ed170b7', 'Acne Treatment'),
('4fb6becf-04ac-11f0-b246-ac198ed170b7', 'Dermatology'),
('4fba68ab-04ac-11f0-b246-ac198ed170b7', 'Hair Restoration');

--
-- Indexes for dumped tables
--

--
-- Indexes for table `appointment`
--
ALTER TABLE `appointment`
  ADD PRIMARY KEY (`id`),
  ADD KEY `PatientID` (`PatientID`),
  ADD KEY `DoctorID` (`DoctorID`);

--
-- Indexes for table `doctor`
--
ALTER TABLE `doctor`
  ADD PRIMARY KEY (`id`),
  ADD KEY `SpecialityID` (`SpecialityID`);

--
-- Indexes for table `medication`
--
ALTER TABLE `medication`
  ADD PRIMARY KEY (`id`);

--
-- Indexes for table `patient`
--
ALTER TABLE `patient`
  ADD PRIMARY KEY (`id`);

--
-- Indexes for table `prescription`
--
ALTER TABLE `prescription`
  ADD PRIMARY KEY (`id`),
  ADD KEY `AppointmentID` (`AppointmentID`),
  ADD KEY `MedicationID` (`MedicationID`);

--
-- Indexes for table `speciality`
--
ALTER TABLE `speciality`
  ADD PRIMARY KEY (`id`);

--
-- AUTO_INCREMENT for dumped tables
--

--
-- AUTO_INCREMENT for table `appointment`
--
ALTER TABLE `appointment`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=20;

--
-- Constraints for dumped tables
--

--
-- Constraints for table `appointment`
--
ALTER TABLE `appointment`
  ADD CONSTRAINT `appointment_ibfk_1` FOREIGN KEY (`PatientID`) REFERENCES `patient` (`id`),
  ADD CONSTRAINT `appointment_ibfk_2` FOREIGN KEY (`DoctorID`) REFERENCES `doctor` (`id`);

--
-- Constraints for table `doctor`
--
ALTER TABLE `doctor`
  ADD CONSTRAINT `doctor_ibfk_1` FOREIGN KEY (`SpecialityID`) REFERENCES `speciality` (`id`);

--
-- Constraints for table `prescription`
--
ALTER TABLE `prescription`
  ADD CONSTRAINT `prescription_ibfk_1` FOREIGN KEY (`AppointmentID`) REFERENCES `appointment` (`id`),
  ADD CONSTRAINT `prescription_ibfk_2` FOREIGN KEY (`MedicationID`) REFERENCES `medication` (`id`);
COMMIT;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
