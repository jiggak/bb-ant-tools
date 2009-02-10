#!/bin/bash
#URL="http://sourceforge.net/export/rss2_projnews.php?group_id=195339"
URL="https://sourceforge.net/export/rss2_projnews.php?group_id=195339&rss_fulltext=1"
wget --output-document=news.xml ${URL}
