ALTER TABLE `wiki_pages` DROP COLUMN `raw_text`;

DROP TABLE `wiki_redirects`;

ALTER TABLE `wiki_pages`  
ADD COLUMN `redirect_page_title` VARCHAR(200) NULL AFTER `is_redirect`,  
ADD COLUMN `redirect_page_id` INT(10) NULL AFTER `redirect_page_title`,  
ADD INDEX `redirect_index` (`redirect_page_id`);

ALTER TABLE `wiki_pages`  
ADD COLUMN `file_offset` INT(20) NOT NULL AFTER `redirect_page_id`,  
ADD INDEX `file_offset_index` (`file_offset`);

ALTER TABLE `wiki_pages` DROP COLUMN `is_redirect`;