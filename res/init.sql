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

CREATE TABLE `wiki_links` (
  `id` int(10) NOT NULL,
  `page_id` int(10) NOT NULL,
  PRIMARY KEY (`id`,`page_id`),
  KEY `from_index` (`id`),
  KEY `to_index` (`page_id`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8;

CREATE TABLE `wiki_redirects` (
  `id` int(10) NOT NULL,
  `page_id` int(10) NOT NULL,
  PRIMARY KEY (`id`),
  KEY `page_id_index` (`page_id`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8;
