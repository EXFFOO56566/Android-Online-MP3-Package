<?php include("includes/connection.php");
 	  include("includes/function.php"); 	
	
	$file_path = 'http://'.$_SERVER['SERVER_NAME'] . dirname($_SERVER['REQUEST_URI']).'/';
 	 
	if(isset($_GET['cat_list']))
 	{
 		$jsonObj= array();
		
		$cat_order=API_CAT_ORDER_BY;


		$query="SELECT cid,category_name,category_image FROM tbl_category WHERE status=1 ORDER BY tbl_category.".$cat_order."";
		$sql = mysqli_query($mysqli,$query)or die(mysql_error());

		while($data = mysqli_fetch_assoc($sql))
		{ 
			$row['cid'] = $data['cid'];
			$row['category_name'] = $data['category_name'];
			$row['category_image'] = $file_path.'images/'.$data['category_image'];
			$row['category_image_thumb'] = $file_path.'images/thumbs/'.$data['category_image'];
  
			array_push($jsonObj,$row);
		
		}

		$set['ONLINE_MP3'] = $jsonObj;
		
		header( 'Content-Type: application/json; charset=utf-8' );
	    echo $val= str_replace('\\/', '/', json_encode($set,JSON_UNESCAPED_UNICODE | JSON_PRETTY_PRINT));
		die();
 	}
	else if(isset($_GET['cat_id']))
	{
		$post_order_by=API_CAT_POST_ORDER_BY;

		$cat_id=$_GET['cat_id'];	

		$jsonObj= array();	
	
	    $query="SELECT * FROM tbl_mp3
		LEFT JOIN tbl_category ON tbl_mp3.cat_id= tbl_category.cid 
		where tbl_mp3.cat_id='".$cat_id."' AND tbl_mp3.status='1' ORDER BY tbl_mp3.id ".$post_order_by."";

		$sql = mysqli_query($mysqli,$query)or die(mysqli_error());

		while($data = mysqli_fetch_assoc($sql))
		{
			$row['id'] = $data['id'];
			$row['cat_id'] = $data['cat_id'];
			$row['mp3_type'] = $data['mp3_type'];
			$row['mp3_title'] = $data['mp3_title'];
			$row['mp3_url'] = $data['mp3_url'];
 			
			 
			$row['mp3_thumbnail_b'] = $file_path.'images/'.$data['mp3_thumbnail'];
			$row['mp3_thumbnail_s'] = $file_path.'images/thumbs/'.$data['mp3_thumbnail'];
			 
			$row['mp3_duration'] = $data['mp3_duration'];
			$row['mp3_artist'] = $data['mp3_artist'];
			$row['mp3_description'] = $data['mp3_description'];
			$row['total_rate'] = $data['total_rate'];
			$row['rate_avg'] = $data['rate_avg'];
			$row['total_views'] = $data['total_views'];
			$row['total_download'] = $data['total_download'];

			$row['cid'] = $data['cid'];
			$row['category_name'] = $data['category_name'];
			$row['category_image'] = $file_path.'images/'.$data['category_image'];
			$row['category_image_thumb'] = $file_path.'images/thumbs/'.$data['category_image'];
			 

			array_push($jsonObj,$row);
		
		}

		$set['ONLINE_MP3'] = $jsonObj;
		
		header( 'Content-Type: application/json; charset=utf-8' );
	    echo $val= str_replace('\\/', '/', json_encode($set,JSON_UNESCAPED_UNICODE | JSON_PRETTY_PRINT));
		die();

		
	}
	else if(isset($_GET['album_list']))
 	{
 		$jsonObj= array();
		 

		$query="SELECT * FROM tbl_album WHERE status='1' ORDER BY tbl_album.aid DESC";
		$sql = mysqli_query($mysqli,$query)or die(mysql_error());

		while($data = mysqli_fetch_assoc($sql))
		{
			 

			$row['aid'] = $data['aid'];
 			$row['album_name'] = $data['album_name'];
			$row['album_image'] = $file_path.'images/'.$data['album_image'];
			$row['album_image_thumb'] = $file_path.'images/thumbs/'.$data['album_image'];
 
			array_push($jsonObj,$row);
		
		}

		$set['ONLINE_MP3'] = $jsonObj;
		
		header( 'Content-Type: application/json; charset=utf-8' );
	    echo $val= str_replace('\\/', '/', json_encode($set,JSON_UNESCAPED_UNICODE | JSON_PRETTY_PRINT));
		die();
 	}
 	else if(isset($_GET['album_id']))
	{
		$post_order_by=API_CAT_POST_ORDER_BY;

		$album_id=$_GET['album_id'];	

		$jsonObj= array();	
	
	    $query="SELECT * FROM tbl_mp3
		LEFT JOIN tbl_category ON tbl_mp3.cat_id= tbl_category.cid
		LEFT JOIN tbl_album ON tbl_mp3.album_id= tbl_album.aid 
		where tbl_mp3.album_id='".$album_id."' AND tbl_mp3.status='1' ORDER BY tbl_mp3.id ".$post_order_by."";

		$sql = mysqli_query($mysqli,$query)or die(mysqli_error());

		while($data = mysqli_fetch_assoc($sql))
		{
			$row['id'] = $data['id'];
			$row['cat_id'] = $data['cat_id'];
			$row['album_id'] = $data['album_id'];
			$row['mp3_type'] = $data['mp3_type'];
			$row['mp3_title'] = $data['mp3_title'];
			$row['mp3_url'] = $data['mp3_url'];
 			
			 
			$row['mp3_thumbnail_b'] = $file_path.'images/'.$data['mp3_thumbnail'];
			$row['mp3_thumbnail_s'] = $file_path.'images/thumbs/'.$data['mp3_thumbnail'];
			 
			$row['mp3_duration'] = $data['mp3_duration'];
			$row['mp3_artist'] = $data['mp3_artist'];
			$row['mp3_description'] = $data['mp3_description'];
			$row['total_rate'] = $data['total_rate'];
			$row['rate_avg'] = $data['rate_avg'];
			$row['total_views'] = $data['total_views'];
			$row['total_download'] = $data['total_download'];

			$row['cid'] = $data['cid'];
			$row['category_name'] = $data['category_name'];
			$row['category_image'] = $file_path.'images/'.$data['category_image'];
			$row['category_image_thumb'] = $file_path.'images/thumbs/'.$data['category_image'];
			
			$row['aid'] = $data['aid'];
 			$row['album_name'] = $data['album_name'];
			$row['album_image'] = $file_path.'images/'.$data['album_image'];
			$row['album_image_thumb'] = $file_path.'images/thumbs/'.$data['album_image']; 

			array_push($jsonObj,$row);
		
		}

		$set['ONLINE_MP3'] = $jsonObj;
		
		header( 'Content-Type: application/json; charset=utf-8' );
	    echo $val= str_replace('\\/', '/', json_encode($set,JSON_UNESCAPED_UNICODE | JSON_PRETTY_PRINT));
		die();

		
	}
	else if(isset($_GET['playlist']))
 	{
 		$jsonObj= array();
		 

		$query="SELECT * FROM tbl_playlist WHERE status='1' ORDER BY tbl_playlist.pid DESC";
		$sql = mysqli_query($mysqli,$query)or die(mysql_error());

		while($data = mysqli_fetch_assoc($sql))
		{
			 

			$row['pid'] = $data['pid'];
 			$row['playlist_name'] = $data['playlist_name'];
			$row['playlist_image'] = $file_path.'images/'.$data['playlist_image'];
			$row['playlist_image_thumb'] = $file_path.'images/thumbs/'.$data['playlist_image'];
 
			array_push($jsonObj,$row);
		
		}

		$set['ONLINE_MP3'] = $jsonObj;
		
		header( 'Content-Type: application/json; charset=utf-8' );
	    echo $val= str_replace('\\/', '/', json_encode($set,JSON_UNESCAPED_UNICODE | JSON_PRETTY_PRINT));
		die();
 	}
 	else if(isset($_GET['playlist_id']))
	{
		$post_order_by=API_CAT_POST_ORDER_BY;

		$playlist_id=$_GET['playlist_id'];	

		$jsonObj= array();	
	
	    $query="SELECT * FROM tbl_playlist
  		where tbl_playlist.status='1' AND pid='".$playlist_id."'";

		$sql = mysqli_query($mysqli,$query)or die(mysqli_error());

		while($data = mysqli_fetch_assoc($sql))
		{
			 
			$row['pid'] = $data['pid'];
 			$row['playlist_name'] = $data['playlist_name'];
			$row['playlist_image'] = $file_path.'images/'.$data['playlist_image'];
			$row['playlist_image_thumb'] = $file_path.'images/thumbs/'.$data['playlist_image']; 

			$songs_list=explode(",", $data['playlist_songs']);

			foreach($songs_list as $song_id)
            {
            	$query1="SELECT * FROM tbl_mp3
				LEFT JOIN tbl_category ON tbl_mp3.cat_id= tbl_category.cid 
				WHERE tbl_mp3.id='".$song_id."' AND tbl_mp3.status='1'";

				$sql1 = mysqli_query($mysqli,$query1)or die(mysqli_error());

				while($data1 = mysqli_fetch_assoc($sql1))
				{
					$row1['id'] = $data1['id'];
					$row1['cat_id'] = $data1['cat_id'];
					$row1['mp3_type'] = $data1['mp3_type'];
					$row1['mp3_title'] = $data1['mp3_title'];
					$row1['mp3_url'] = $data1['mp3_url'];
		 			
					 
					$row1['mp3_thumbnail_b'] = $file_path.'images/'.$data1['mp3_thumbnail'];
					$row1['mp3_thumbnail_s'] = $file_path.'images/thumbs/'.$data1['mp3_thumbnail'];
					 
					$row1['mp3_duration'] = $data1['mp3_duration'];
					$row1['mp3_artist'] = $data1['mp3_artist'];
					$row1['mp3_description'] = $data1['mp3_description'];
					$row1['total_rate'] = $data1['total_rate'];
					$row1['rate_avg'] = $data1['rate_avg'];
					$row1['total_views'] = $data1['total_views'];
					$row1['total_download'] = $data1['total_download'];

					$row1['cid'] = $data1['cid'];
					$row1['category_name'] = $data1['category_name'];
					$row1['category_image'] = $file_path.'images/'.$data1['category_image'];
					$row1['category_image_thumb'] = $file_path.'images/thumbs/'.$data1['category_image'];
			 
					 
					$row['songs_list'][]=$row1;
				}
            }

			array_push($jsonObj,$row);
		
		}

		$set['ONLINE_MP3'] = $jsonObj;
		
		header( 'Content-Type: application/json; charset=utf-8' );
	    echo $val= str_replace('\\/', '/', json_encode($set,JSON_UNESCAPED_UNICODE | JSON_PRETTY_PRINT));
		die();

		
	}	 
	else if(isset($_GET['latest']))
	{
		//$limit=$_GET['latest'];	 

		$limit=API_LATEST_LIMIT;

		$jsonObj= array();	
   
		$query="SELECT * FROM tbl_mp3
		LEFT JOIN tbl_category ON tbl_mp3.cat_id= tbl_category.cid 
		WHERE tbl_mp3.status='1' ORDER BY tbl_mp3.id DESC LIMIT $limit";

		$sql = mysqli_query($mysqli,$query)or die(mysqli_error());

		while($data = mysqli_fetch_assoc($sql))
		{
			$row['id'] = $data['id'];
			$row['cat_id'] = $data['cat_id'];
			$row['mp3_type'] = $data['mp3_type'];
			$row['mp3_title'] = $data['mp3_title'];
			$row['mp3_url'] = $data['mp3_url'];
 			
			 
			$row['mp3_thumbnail_b'] = $file_path.'images/'.$data['mp3_thumbnail'];
			$row['mp3_thumbnail_s'] = $file_path.'images/thumbs/'.$data['mp3_thumbnail'];
			 
			$row['mp3_duration'] = $data['mp3_duration'];
			$row['mp3_artist'] = $data['mp3_artist'];
			$row['mp3_description'] = $data['mp3_description'];
			$row['total_rate'] = $data['total_rate'];
			$row['rate_avg'] = $data['rate_avg'];
			$row['total_views'] = $data['total_views'];
			$row['total_download'] = $data['total_download'];

			$row['cid'] = $data['cid'];
			$row['category_name'] = $data['category_name'];
			$row['category_image'] = $file_path.'images/'.$data['category_image'];
			$row['category_image_thumb'] = $file_path.'images/thumbs/'.$data['category_image'];
			 

			array_push($jsonObj,$row);
		
		}

		$set['ONLINE_MP3'] = $jsonObj;
		
		header( 'Content-Type: application/json; charset=utf-8' );
	    echo $val= str_replace('\\/', '/', json_encode($set,JSON_UNESCAPED_UNICODE | JSON_PRETTY_PRINT));
		die();

	} 
	else if(isset($_GET['search_text']))
	{
		//$limit=$_GET['latest'];	 

 
		$jsonObj= array();	
   
		$query="SELECT * FROM tbl_mp3
		LEFT JOIN tbl_category ON tbl_mp3.cat_id= tbl_category.cid
		WHERE tbl_mp3.status='1' AND tbl_mp3.mp3_title like '%".addslashes($_GET['search_text'])."%' 
		ORDER BY tbl_mp3.mp3_title";

		$sql = mysqli_query($mysqli,$query)or die(mysqli_error());

		while($data = mysqli_fetch_assoc($sql))
		{
			$row['id'] = $data['id'];
			$row['cat_id'] = $data['cat_id'];
			$row['mp3_type'] = $data['mp3_type'];
			$row['mp3_title'] = $data['mp3_title'];
			$row['mp3_url'] = $data['mp3_url'];
 			
			 
			$row['mp3_thumbnail_b'] = $file_path.'images/'.$data['mp3_thumbnail'];
			$row['mp3_thumbnail_s'] = $file_path.'images/thumbs/'.$data['mp3_thumbnail'];
			 
			$row['mp3_duration'] = $data['mp3_duration'];
			$row['mp3_artist'] = $data['mp3_artist'];
			$row['mp3_description'] = $data['mp3_description'];
			$row['total_rate'] = $data['total_rate'];
			$row['rate_avg'] = $data['rate_avg'];
			$row['total_views'] = $data['total_views'];
			$row['total_download'] = $data['total_download'];

			$row['cid'] = $data['cid'];
			$row['category_name'] = $data['category_name'];
			$row['category_image'] = $file_path.'images/'.$data['category_image'];
			$row['category_image_thumb'] = $file_path.'images/thumbs/'.$data['category_image'];			 

			array_push($jsonObj,$row);
		
		}

		$set['ONLINE_MP3'] = $jsonObj;
		
		header( 'Content-Type: application/json; charset=utf-8' );
	    echo $val= str_replace('\\/', '/', json_encode($set,JSON_UNESCAPED_UNICODE | JSON_PRETTY_PRINT));
		die();

	}
	else if(isset($_GET['mp3_id']))
	{
		  
				 
		$jsonObj= array();	

		 
		$query="SELECT * FROM tbl_mp3
		LEFT JOIN tbl_category ON tbl_mp3.cat_id= tbl_category.cid 
		WHERE tbl_mp3.id='".$_GET['mp3_id']."' AND tbl_mp3.status='1'";

		$sql = mysqli_query($mysqli,$query)or die(mysqli_error());

		while($data = mysqli_fetch_assoc($sql))
		{
			$row['id'] = $data['id'];
			$row['cat_id'] = $data['cat_id'];
			$row['mp3_type'] = $data['mp3_type'];
			$row['mp3_title'] = $data['mp3_title'];
			$row['mp3_url'] = $data['mp3_url'];
 			
			 
			$row['mp3_thumbnail_b'] = $file_path.'images/'.$data['mp3_thumbnail'];
			$row['mp3_thumbnail_s'] = $file_path.'images/thumbs/'.$data['mp3_thumbnail'];
			 
			$row['mp3_duration'] = $data['mp3_duration'];
			$row['mp3_artist'] = $data['mp3_artist'];
			$row['mp3_description'] = $data['mp3_description'];
			$row['total_rate'] = $data['total_rate'];
			$row['rate_avg'] = $data['rate_avg'];
			$row['total_views'] = $data['total_views'];
			$row['total_download'] = $data['total_download'];

			$row['cid'] = $data['cid'];
			$row['category_name'] = $data['category_name'];
			$row['category_image'] = $file_path.'images/'.$data['category_image'];
			$row['category_image_thumb'] = $file_path.'images/thumbs/'.$data['category_image'];
			 
			if(isset($_GET['device_id']))
			{
				$query1 = mysqli_query($mysqli,"select * from tbl_rating where post_id  = '".$_GET['mp3_id']."' && ip = '".$_GET['device_id']."' ");
				$data1 = mysqli_fetch_assoc($query1);

				if(@count($data1) != 0 ){
					$row['user_rate'] = $data1['rate'];
				}
				else
				{
					$row['user_rate'] = 0;
				}
			}
			 
			array_push($jsonObj,$row);
		
		}
 		
 		$view_qry=mysqli_query($mysqli,"UPDATE tbl_mp3 SET total_views = total_views + 1 WHERE id = '".$_GET['mp3_id']."'");

		$set['ONLINE_MP3'] = $jsonObj;
		
		header( 'Content-Type: application/json; charset=utf-8' );
	    echo $val= str_replace('\\/', '/', json_encode($set,JSON_UNESCAPED_UNICODE | JSON_PRETTY_PRINT));
		die();	
 

	}
	else if(isset($_GET['recent_artist_list']))
 	{
 		$jsonObj= array();
		 

		$query="SELECT id,artist_name,artist_image FROM tbl_artist ORDER BY tbl_artist.id DESC LIMIT 10";
		$sql = mysqli_query($mysqli,$query)or die(mysql_error());

		while($data = mysqli_fetch_assoc($sql))
		{
			 

			$row['id'] = $data['id'];
			$row['artist_name'] = $data['artist_name'];
			$row['artist_image'] = $file_path.'images/'.$data['artist_image'];
			$row['artist_image_thumb'] = $file_path.'images/thumbs/'.$data['artist_image'];
 
			array_push($jsonObj,$row);
		
		}

		$set['ONLINE_MP3'] = $jsonObj;
		
		header( 'Content-Type: application/json; charset=utf-8' );
	    echo $val= str_replace('\\/', '/', json_encode($set,JSON_UNESCAPED_UNICODE | JSON_PRETTY_PRINT));
		die();
 	}
	else if(isset($_GET['artist_list']))
 	{
 		$jsonObj= array();
		
		$cat_order=API_CAT_ORDER_BY;


		$query="SELECT id,artist_name,artist_image FROM tbl_artist ORDER BY tbl_artist.id";
		$sql = mysqli_query($mysqli,$query)or die(mysql_error());

		while($data = mysqli_fetch_assoc($sql))
		{
			 

			$row['id'] = $data['id'];
			$row['artist_name'] = $data['artist_name'];
			$row['artist_image'] = $file_path.'images/'.$data['artist_image'];
			$row['artist_image_thumb'] = $file_path.'images/thumbs/'.$data['artist_image'];
 
			array_push($jsonObj,$row);
		
		}

		$set['ONLINE_MP3'] = $jsonObj;
		
		header( 'Content-Type: application/json; charset=utf-8' );
	    echo $val= str_replace('\\/', '/', json_encode($set,JSON_UNESCAPED_UNICODE | JSON_PRETTY_PRINT));
		die();
 	}
 	else if(isset($_GET['artist_name']))
	{
		$post_order_by=API_CAT_POST_ORDER_BY;

		$artist_name=$_GET['artist_name'];	

		$jsonObj= array();	
	
	    $query="SELECT * FROM tbl_mp3
		LEFT JOIN tbl_category ON tbl_mp3.cat_id= tbl_category.cid
 		WHERE tbl_mp3.mp3_artist LIKE '%".$artist_name."%' AND tbl_mp3.status='1' ORDER BY tbl_mp3.id DESC";

		$sql = mysqli_query($mysqli,$query)or die(mysqli_error());

		while($data = mysqli_fetch_assoc($sql))
		{
			$row['id'] = $data['id'];
			$row['cat_id'] = $data['cat_id'];
			$row['mp3_type'] = $data['mp3_type'];
			$row['mp3_title'] = $data['mp3_title'];
			$row['mp3_url'] = $data['mp3_url'];
 			
			 
			$row['mp3_thumbnail_b'] = $file_path.'images/'.$data['mp3_thumbnail'];
			$row['mp3_thumbnail_s'] = $file_path.'images/thumbs/'.$data['mp3_thumbnail'];
			 
			$row['mp3_duration'] = $data['mp3_duration'];
			$row['mp3_artist'] = $data['mp3_artist'];
			$row['mp3_description'] = $data['mp3_description'];
			$row['total_rate'] = $data['total_rate'];
			$row['rate_avg'] = $data['rate_avg'];
			$row['total_views'] = $data['total_views'];
			$row['total_download'] = $data['total_download'];
			
			$row['cid'] = $data['cid'];
			$row['category_name'] = $data['category_name'];
			$row['category_image'] = $file_path.'images/'.$data['category_image'];
			$row['category_image_thumb'] = $file_path.'images/thumbs/'.$data['category_image'];
			 

			array_push($jsonObj,$row);
		
		}

		$set['ONLINE_MP3'] = $jsonObj;
		
		header( 'Content-Type: application/json; charset=utf-8' );
	    echo $val= str_replace('\\/', '/', json_encode($set,JSON_UNESCAPED_UNICODE | JSON_PRETTY_PRINT));
		die();

		
	}
	else if(isset($_GET['song_download_id']))
	{
		$jsonObj= array();		
		 
		$view_qry=mysqli_query($mysqli,"UPDATE tbl_mp3 SET total_download = total_download + 1 WHERE id = '".$_GET['song_download_id']."'");
 	 

    	$total_dw_sql="SELECT * FROM tbl_mp3 WHERE id='".$_GET['song_download_id']."'";
	    $total_dw_res=mysqli_query($mysqli,$total_dw_sql);
	    $total_dw_row=mysqli_fetch_assoc($total_dw_res);
	    
	         
        $jsonObj = array( 'total_download' => $total_dw_row['total_download']);

        $set['HD_WALLPAPER'][] = $jsonObj;
        header( 'Content-Type: application/json; charset=utf-8' );
	    echo $val= str_replace('\\/', '/', json_encode($set,JSON_UNESCAPED_UNICODE | JSON_PRETTY_PRINT));
		die();
	}	  	 
	else 
	{
		$jsonObj= array();	

		$query="SELECT * FROM tbl_settings WHERE id='1'";
		$sql = mysqli_query($mysqli,$query)or die(mysqli_error());

		while($data = mysqli_fetch_assoc($sql))
		{
			 
			$row['app_name'] = $data['app_name'];
			$row['app_logo'] = $data['app_logo'];
			$row['app_version'] = $data['app_version'];
			$row['app_author'] = $data['app_author'];
			$row['app_contact'] = $data['app_contact'];
			$row['app_email'] = $data['app_email'];
			$row['app_website'] = $data['app_website'];
			$row['app_description'] = stripslashes($data['app_description']);
 			$row['app_developed_by'] = $data['app_developed_by'];

			$row['app_privacy_policy'] = stripslashes($data['app_privacy_policy']);

			$row['publisher_id'] = $data['publisher_id'];
			$row['interstital_ad'] = $data['interstital_ad'];
			$row['interstital_ad_id'] = $data['interstital_ad_id'];
			$row['interstital_ad_click'] = $data['interstital_ad_click'];
 			$row['banner_ad'] = $data['banner_ad'];
 			$row['banner_ad_id'] = $data['banner_ad_id'];

 			$row['song_download'] = $data['song_download'];


			array_push($jsonObj,$row);
		
		}

		$set['ONLINE_MP3'] = $jsonObj;
		
		header( 'Content-Type: application/json; charset=utf-8' );
	    echo $val= str_replace('\\/', '/', json_encode($set,JSON_UNESCAPED_UNICODE | JSON_PRETTY_PRINT));
		die();	
	}		
	 
	 
?>