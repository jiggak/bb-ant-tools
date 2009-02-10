<?php
include_once "markdown/markdown.php";

include 'header.php';

// extract page name from request and replace unwanted prefix
$page = $_SERVER['REQUEST_URI'];
$page = str_replace("/" ,"", $page);

// use 'main' as default when page is empty
if (empty($page))
  $page = "main";

// convert page name to path
$page_file = "pages/" . $page . ".txt";

// process file with markdown engine if exist, otherwise display error
if (file_exists($page_file)) {
  $text = file_get_contents($page_file);
  print(Markdown($text));

  // append news feed to main page
  if ($page == "main")
    include 'news.php';
} else {
  print ("<h2>File Not Found</h2>");
}

include 'footer.php';
?>
