<?php
require_once('magpierss/rss_fetch.inc');
$rss_string = file_get_contents('news.xml');
$rss = new MagpieRSS($rss_string);
?>

<h2>News</h2>

<p>
<? if (count($rss->items) == 0) { ?>
No news right now
<? } ?>

<? foreach ($rss->items as $item ) { ?>
<div class="news_item">
  <div><a class="news_link" href="<?= $item[link] ?>"><?= $item[title] ?></a></div>
  <span class="news_date"><?= $item[pubdate] ?></span><br />
  <p><?= $item[description] ?></p>
</div>
<? } ?>
</p>

