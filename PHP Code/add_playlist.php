<?php include("includes/header.php");

	require("includes/function.php");
	require("language/language.php");

	require_once("thumbnail_images.class.php");

     $mp3_qry="SELECT * FROM tbl_mp3 ORDER BY tbl_mp3.id DESC"; 
     $mp3_result=mysqli_query($mysqli,$mp3_qry); 
	 
	if(isset($_POST['submit']) and isset($_GET['add']))
	{
	
	   $playlist_image=rand(0,99999)."_".$_FILES['playlist_image']['name'];
		 	 
       //Main Image
	   $tpath1='images/'.$playlist_image; 			 
       $pic1=compress_image($_FILES["playlist_image"]["tmp_name"], $tpath1, 80);
	 
		//Thumb Image 
	   $thumbpath='images/thumbs/'.$playlist_image;		
       $thumb_pic1=create_thumb_image($tpath1,$thumbpath,'300','300');   
 
          
       $data = array( 
 			   'playlist_name'  =>  $_POST['playlist_name'],
			   'playlist_image'  =>  $playlist_image,
         'playlist_songs'  =>  implode(',',$_POST['playlist_songs'])
			    );		

 		$qry = Insert('tbl_playlist',$data);	

 	    
		$_SESSION['msg']="10";
 
		header( "Location:manage_playlist.php");
		exit;	

		 
		
	}
	
	if(isset($_GET['playlist_id']))
	{
			 
			$qry="SELECT * FROM tbl_playlist where pid='".$_GET['playlist_id']."'";
			$result=mysqli_query($mysqli,$qry);
			$row=mysqli_fetch_assoc($result);

	}
	if(isset($_POST['submit']) and isset($_POST['playlist_id']))
	{
		 
		 if($_FILES['playlist_image']['name']!="")
		 {		


				$img_res=mysqli_query($mysqli,'SELECT * FROM tbl_playlist WHERE pid='.$_GET['playlist_id'].'');
			    $img_res_row=mysqli_fetch_assoc($img_res);
			

			    if($img_res_row['playlist_image']!="")
		        {
					unlink('images/thumbs/'.$img_res_row['playlist_image']);
					unlink('images/'.$img_res_row['playlist_image']);
			     }

 				   $playlist_image=rand(0,99999)."_".$_FILES['playlist_image']['name'];
		 	 
			       //Main Image
				   $tpath1='images/'.$playlist_image; 			 
			       $pic1=compress_image($_FILES["playlist_image"]["tmp_name"], $tpath1, 80);
				 
					//Thumb Image 
				   $thumbpath='images/thumbs/'.$playlist_image;		
			       $thumb_pic1=create_thumb_image($tpath1,$thumbpath,'300','300');

                    $data = array(
 					    'playlist_name'  =>  $_POST['playlist_name'],
					    'playlist_image'  =>  $playlist_image,
              'playlist_songs'  =>  implode(',',$_POST['playlist_songs'])
						);

					$category_edit=Update('tbl_playlist', $data, "WHERE pid = '".$_POST['playlist_id']."'");

		 }
		 else
		 {

					 $data = array(
 			          'playlist_name'  =>  $_POST['playlist_name'],
                'playlist_songs'  =>  implode(',',$_POST['playlist_songs'])
						);	
 
			         $artist_edit=Update('tbl_playlist', $data, "WHERE pid = '".$_POST['playlist_id']."'");

		 }
 
		
		$_SESSION['msg']="11"; 
		header( "Location:add_playlist.php?playlist_id=".$_POST['playlist_id']);
		exit;
 
	}


?>
<div class="row">
      <div class="col-md-12">
        <div class="card">
          <div class="page_title_block">
            <div class="col-md-5 col-xs-12">
              <div class="page_title"><?php if(isset($_GET['cat_id'])){?>Edit<?php }else{?>Add<?php }?> Playlist</div>
            </div>
          </div>
          <div class="clearfix"></div>
          <div class="row mrg-top">
            <div class="col-md-12">
               
              <div class="col-md-12 col-sm-12">
                <?php if(isset($_SESSION['msg'])){?> 
               	 <div class="alert alert-success alert-dismissible" role="alert"> <button type="button" class="close" data-dismiss="alert" aria-label="Close"><span aria-hidden="true">×</span></button>
                	<?php echo $client_lang[$_SESSION['msg']] ; ?></a> </div>
                <?php unset($_SESSION['msg']);}?>	
              </div>
            </div>
          </div>
          <div class="card-body mrg_bottom"> 
            <form action="" name="addeditcategory" method="post" class="form form-horizontal" enctype="multipart/form-data">
            	<input  type="hidden" name="playlist_id" value="<?php echo $_GET['playlist_id'];?>" />

              <div class="section">
                <div class="section-body">
               
                  <div class="form-group">
                    <label class="col-md-3 control-label">Playlist Name :-</label>
                    <div class="col-md-6">
                      <input type="text" name="playlist_name" id="playlist_name" value="<?php if(isset($_GET['playlist_id'])){echo $row['playlist_name'];}?>" class="form-control" required>
                    </div>
                  </div>
                  <div class="form-group">
                    <label class="col-md-3 control-label">Playlist Image :-
                    	<p class="control-label-help">(Recommended resolution: 300x300,400x400 or Square Image)</p>
                    </label>
                    <div class="col-md-6">
                      <div class="fileupload_block">
                        <input type="file" name="playlist_image" value="fileupload" id="fileupload">
                             
                        	  <div class="fileupload_img"><img type="image" src="assets/images/add-image.png" alt="category image" /></div>
                        	 
                      </div>
                    </div>
                  </div>
                  <div class="form-group">
                    <label class="col-md-3 control-label">&nbsp; </label>
                    <div class="col-md-6">
                      <?php if(isset($_GET['playlist_id']) and $row['playlist_image']!="") {?>
                            <div class="block_wallpaper"><img src="images/<?php echo $row['playlist_image'];?>" alt="image" /></div>
                          <?php } ?>
                    </div>
                  </div><br>
                  <div class="form-group">
                    <label class="col-md-3 control-label">Songs :-</label>
                    <div class="col-md-6">
                      <select name="playlist_songs[]" id="playlist_songs" class="select2 form-control" required multiple="multiple">
                        <option value="">--Select Songs--</option>
                        <?php
                            while($mp3_row=mysqli_fetch_array($mp3_result))
                            {
                        ?>   
                        <?php if(isset($_GET['playlist_id'])){?>

                           <option value="<?php echo $mp3_row['id'];?>" <?php $songs_list=explode(",", $row['playlist_songs']);
                  foreach($songs_list as $song_id)
                {if($mp3_row['id']==$song_id){?>selected<?php }}?>><?php echo $mp3_row['mp3_title'];?></option>
                            
                          </option>   

                        <?php }else{?>  

                          <option value="<?php echo $mp3_row['id'];?>"><?php echo $mp3_row['mp3_title'];?></option>
                            
                        <?php }?>   
                         
                        <?php
                          }
                        ?>
                      </select>
                    </div>
                  </div>
                  <div class="form-group">
                    <div class="col-md-9 col-md-offset-3">
                      <button type="submit" name="submit" class="btn btn-primary">Save</button>
                    </div>
                  </div>
                </div>
              </div>
            </form>
          </div>
        </div>
      </div>
    </div>
        
<?php include("includes/footer.php");?>       
