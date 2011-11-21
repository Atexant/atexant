CREATE TABLE `wiki_pages` (
  `id` int(10) NOT NULL,
  `title` varchar(100) NOT NULL,
  `raw_text` mediumtext NOT NULL,
  `is_redirect` tinyint(4) NOT NULL DEFAULT '0',
  PRIMARY KEY (`id`),
  UNIQUE KEY `title_id_index` (`title`,`id`),
  KEY `title_index` (`title`),
  FULLTEXT KEY `raw_text_index` (`raw_text`),
  FULLTEXT KEY `title_fulltext_index` (`title`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8 ROW_FORMAT=FIXED;
