CREATE TABLE WebDocumentEntry(PRIMARY KEY(url),
	doc_type INT,
	title VARCHAR(255),
	url VARCHAR(255),
	bookmark_count INT,
	view_user_count INT,
	crawledDate DATE,
	category VARCHAR(255),
	Description TEXT,
	isAnalyzed INT
);

CREATE TABLE UserEntry(PRIMARY KEY(id),
	id VARCHAR(255) UNIQUE,
	password VARCHAR(255),
	name VARCHAR(255),
	mail_address VARCHAR(255),
	age INT,
	regist_date DATE,
	cache_date DATE
);

CREATE TABLE WebDocument_keyword_table(PRIMARY KEY(doc_address ,keyword),
	doc_address VARCHAR(255),
	keyword VARCHAR(255),
	tfidf_value REAL
);

CREATE TABLE User_keyword_table(PRIMARY KEY(user_id ,keyword),
	user_id VARCHAR(255),
	keyword VARCHAR(255),
	tfidf_value REAL
);

CREATE TABLE WebDocument_tag_table(PRIMARY KEY(doc_address ,tag),
	doc_address VARCHAR(255),
	tag VARCHAR(255),
	tfidf_value REAL
);

CREATE TABLE UserViewedEntries(
	id VARCHAR(255),
	doc_url VARCHAR(255)
);


CREATE INDEX idxWebDocumentEntryurl on WebDocumentEntry(url);
CREATE INDEX idxWebDocument_keyword_tabledoc_address on WebDocument_keyword_table(doc_address);
CREATE INDEX idxWebDocument_keyword_tablekeyword on WebDocument_keyword_table(keyword);
CREATE INDEX idxUser_keyword_tableuser_id on User_keyword_table(user_id);
CREATE INDEX idxUser_keyword_tablekeyword on User_keyword_table(keyword);
CREATE INDEX idxWebDocument_tag_tabledoc_address on WebDocument_tag_table(doc_address);
CREATE INDEX idxWebDocument_tag_tabletag on WebDocument_tag_table(tag);
