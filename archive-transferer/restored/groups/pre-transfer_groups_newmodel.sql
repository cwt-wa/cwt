-- phpMyAdmin SQL Dump
-- version 3.2.4
-- http://www.phpmyadmin.net
--
-- Host: localhost
-- Generation Time: Jul 28, 2013 at 04:16 PM
-- Server version: 5.1.44
-- PHP Version: 5.3.1

SET SQL_MODE="NO_AUTO_VALUE_ON_ZERO";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8 */;

--
-- Database: `cwt_archive`
--

-- --------------------------------------------------------

--
-- Table structure for table `groups`
--

CREATE TABLE IF NOT EXISTS `groups` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `tournament_id` int(11) NOT NULL,
  `group` set('A','B','C','D','E','F','G','H') COLLATE utf8_bin NOT NULL,
  `user_id` int(11) NOT NULL,
  `points` tinyint(4) NOT NULL,
  `games` tinyint(4) NOT NULL,
  `game_ratio` tinyint(4) NOT NULL,
  `round_ratio` tinyint(4) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB  DEFAULT CHARSET=utf8 COLLATE=utf8_bin AUTO_INCREMENT=33 ;

--
-- Dumping data for table `groups`
--

INSERT INTO `groups` (`id`, `tournament_id`, `group`, `user_id`, `points`, `games`, `game_ratio`, `round_ratio`) VALUES
(1, 11, 'A', 24, 9, 3, 3, 8),
(2, 11, 'A', 50, 6, 3, 1, 1),
(3, 11, 'A', 31, 1, 3, -3, -5),
(4, 11, 'A', 22, 3, 3, -1, -4),
(5, 11, 'B', 55, 9, 3, 3, 7),
(6, 11, 'B', 37, 0, 3, -3, -8),
(7, 11, 'B', 15, 3, 3, -1, -1),
(8, 11, 'B', 174, 6, 3, 1, 2),
(9, 11, 'C', 13, 7, 3, 1, 4),
(10, 11, 'C', 18, 3, 3, -1, -4),
(11, 11, 'C', 42, 9, 3, 3, 7),
(12, 11, 'C', 180, 1, 3, -3, -7),
(13, 11, 'D', 30, 7, 3, 1, 5),
(14, 11, 'D', 12, 6, 3, 1, 2),
(15, 11, 'D', 57, 6, 3, 1, 1),
(16, 11, 'D', 56, 0, 3, -3, -8),
(17, 11, 'E', 10, 6, 3, 1, 2),
(18, 11, 'E', 20, 9, 3, 3, 7),
(19, 11, 'E', 8, 0, 3, -3, -7),
(20, 11, 'E', 159, 3, 3, -1, -2),
(21, 11, 'F', 2, 9, 3, 3, 7),
(22, 11, 'F', 17, 6, 3, 1, 2),
(23, 11, 'F', 6, 4, 3, -1, 0),
(24, 11, 'F', 32, 0, 3, -3, -9),
(25, 11, 'G', 46, 9, 3, 3, 7),
(26, 11, 'G', 5, 7, 3, 1, 1),
(27, 11, 'G', 54, 1, 3, -3, -6),
(28, 11, 'G', 158, 4, 3, -1, -2),
(29, 11, 'H', 11, 9, 3, 3, 8),
(30, 11, 'H', 16, 6, 3, 1, 0),
(31, 11, 'H', 26, 1, 3, -3, -5),
(32, 11, 'H', 160, 3, 3, -1, -3);
