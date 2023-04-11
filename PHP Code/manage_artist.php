<?php include("includes/header.php");

	require("includes/function.php");
	require("language/language.php");

     if(isset($_POST['data_search']))
   {
       
     $artist_qry="SELECT * FROM tbl_artist 
     WHERE tbl_artist.artist_name like '%".addslashes($_POST['search_text'])."%'  
     ORDER BY tbl_artist.artist_name"; 

     $result=mysqli_query($mysqli,$artist_qry);

   }
   else
   {
 

      $tableName="tbl_artist";   
      $targetpage = "manage_artist.php"; 
      $limit = 8; 
      
      $query = "SELECT COUNT(*) as num FROM $tableName";
      $total_pages = mysqli_fetch_array(mysqli_query($mysqli,$query));
      $total_pages = $total_pages['num'];
      
      $stages = 3;
      $page=0;
      if(isset($_GET['page'])){
      $page = mysqli_real_escape_string($mysqli,$_GET['page']);
      }
      if($page){
        $start = ($page - 1) * $limit; 
      }else{
        $start = 0; 
        } 
      
     $artist_qry="SELECT * FROM tbl_artist ORDER BY tbl_artist.artist_name LIMIT $start, $limit"; 
     $result=mysqli_query($mysqli,$artist_qry);
   }
	
	if(isset($_GET['artist_id']))
	{

	 
		$cat_res=mysqli_query($mysqli,'SELECT * FROM tbl_artist WHERE id=\''.$_GET['artist_id'].'\'');
		$cat_res_row=mysqli_fetch_assoc($cat_res);


		if($cat_res_row['artist_image']!="")
	    {
	    	unlink('images/'.$cat_res_row['artist_image']);
			  unlink('images/thumbs/'.$cat_res_row['artist_image']);

		}
 
		Delete('tbl_artist','id='.$_GET['artist_id'].'');

      
		$_SESSION['msg']="12";
		header( "Location:manage_artist.php");
		exit;
		
	}	
	 
?>
                
    <div class="row">
      <div class="col-xs-12">
        <div class="card mrg_bottom">
          <div class="page_title_block">
            <div class="col-md-5 col-xs-12">
              <div class="page_title">Manage Artist</div>
            </div>
            <div class="col-md-7 col-xs-12">
              <div class="search_list">
                <div class="search_block">
                <form  method="post" action="">
                        <input class="form-control input-sm" placeholder="Search artist..." aria-controls="DataTables_Table_0" type="search" name="search_text" required>
                        <button type="submit" name="data_search" class="btn-search"><i class="fa fa-search"></i></button>
                </form>
              </div>
                <div class="add_btn_primary"> <a href="add_artist.php?add=yes">Add Artist</a> </div>
              </div>
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
           
          <div class="col-md-12 mrg-top">
            <div class="row">
              <?php 
              $i=0;
              while($row=mysqli_fetch_array($result))
              {         
          ?>
              <div class="col-lg-3 col-sm-6 col-xs-12">
                <div class="block_wallpaper">           
                  <div class="wall_image_title">
                    <h2><a href="#"><?php echo $row['artist_name'];?></a></h2>
                    <ul>                
                      <li><a href="add_artist.php?artist_id=<?php echo $row['id'];?>" data-toggle="tooltip" data-tooltip="Edit"><i class="fa fa-edit"></i></a></li>               
                      <li><a href="?artist_id=<?php echo $row['id'];?>" data-toggle="tooltip" data-tooltip="Delete" onclick="return confirm('Are you sure you want to delete this artist?');"><i class="fa fa-trash"></i></a></li>
                       
                    </ul>
                  </div>
                  <span><img src="images/<?php echo $row['artist_image'];?>" /></span>
                </div>
              </div>
          <?php
            
            $i++;
              }
        ?>     
               
      </div>
          </div>

          <div class="col-md-12 col-xs-12">
            <div class="pagination_item_block">
              <nav>
                <?php if(!isset($_POST["data_search"])){ include("pagination.php");}?>
              </nav>
            </div>
          </div> 
          <div class="clearfix"></div>
        </div>
      </div>
    </div>
        
<?php include("includes/footer.php");?>       
