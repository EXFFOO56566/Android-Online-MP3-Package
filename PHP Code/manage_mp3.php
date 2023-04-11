<?php include("includes/header.php");

	require("includes/function.php");
	require("language/language.php");

	  if(isset($_POST['data_search']))
   {
      $mp3_qry="SELECT * FROM tbl_mp3
                  LEFT JOIN tbl_category ON tbl_mp3.cat_id= tbl_category.cid 
                  WHERE tbl_mp3.mp3_title like '%".addslashes($_POST['mp3_title'])."%'  
                  ORDER BY tbl_mp3.mp3_title";
 
     $result=mysqli_query($mysqli,$mp3_qry);

   }
   else
   {

	   //Get all mp3 
	
      $tableName="tbl_mp3";   
      $targetpage = "manage_mp3.php"; 
      $limit = 12; 
      
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
      
     $mp3_qry="SELECT * FROM tbl_mp3
                  LEFT JOIN tbl_category ON tbl_mp3.cat_id= tbl_category.cid 
                  ORDER BY tbl_mp3.id DESC LIMIT $start, $limit";
 
     $result=mysqli_query($mysqli,$mp3_qry); 
	 }

  if(isset($_GET['mp3_id']))
  { 

    $img_res=mysqli_query($mysqli,'SELECT * FROM tbl_mp3 WHERE id='.$_GET['mp3_id'].'');
    $img_res_row=mysqli_fetch_assoc($img_res);
           
    if($img_res_row['mp3_thumbnail']!="")
     {
          unlink('images/thumbs/'.$img_res_row['mp3_thumbnail']);
          unlink('images/'.$img_res_row['mp3_thumbnail']);

          unlink($img_res_row['mp3_url']);
      }
 
    Delete('tbl_mp3','id='.$_GET['mp3_id'].'');
    
    $_SESSION['msg']="12";
    header( "Location:manage_mp3.php");
    exit;
    
  }

  //Active and Deactive status
if(isset($_GET['status_deactive_id']))
{
   $data = array('status'  =>  '0');
  
   $edit_status=Update('tbl_mp3', $data, "WHERE id = '".$_GET['status_deactive_id']."'");
  
   $_SESSION['msg']="14";
   header( "Location:manage_mp3.php");
   exit;
}
if(isset($_GET['status_active_id']))
{
    $data = array('status'  =>  '1');
    
    $edit_status=Update('tbl_mp3', $data, "WHERE id = '".$_GET['status_active_id']."'");
    
    $_SESSION['msg']="13";   
    header( "Location:manage_mp3.php");
    exit;
}  

?>
                
    <div class="row">
      <div class="col-xs-12">
        <div class="card mrg_bottom">
          <div class="page_title_block">
            <div class="col-md-5 col-xs-12">
              <div class="page_title">Manage Mp3</div>
            </div>
            <div class="col-md-7 col-xs-12">
              <div class="search_list">
                <div class="search_block">
                <form  method="post" action="">
                        <input class="form-control input-sm" placeholder="Search songs..." aria-controls="DataTables_Table_0" type="search" name="mp3_title" required>
                        <button type="submit" name="data_search" class="btn-search"><i class="fa fa-search"></i></button>
                </form>
              </div>
                <div class="add_btn_primary"> <a href="add_mp3.php">Add Mp3</a> </div>
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
                  <div class="wall_category_block">
                    <h2><?php echo $row['category_name'];?></h2>              
                  </div>
                  <div class="wall_image_title">
                     <p><?php echo $row['mp3_title'];?></p>
                    <ul>
                      <li><a href="javascript:void(0)" data-toggle="tooltip" data-tooltip="<?php echo $row['total_views'];?> Views"><i class="fa fa-eye"></i></a></li>                      
                      <li><a href="javascript:void(0)" data-toggle="tooltip" data-tooltip="<?php echo $row['total_download'];?> Download"><i class="fa fa-download"></i></a></li>
                      <li><a href="javascript:void(0)" data-toggle="tooltip" data-tooltip="<?php echo $row['rate_avg'];?> Rating"><i class="fa fa-star"></i></a></li>

                      <li><a href="edit_mp3.php?mp3_id=<?php echo $row['id'];?>" data-toggle="tooltip" data-tooltip="Edit"><i class="fa fa-edit"></i></a></li>
                      <li><a href="?mp3_id=<?php echo $row['id'];?>" data-toggle="tooltip" data-tooltip="Delete" onclick="return confirm('Are you sure you want to delete this song?');"><i class="fa fa-trash"></i></a></li>
                    </ul>
                  </div>
                  <span><img src="images/<?php echo $row['mp3_thumbnail'];?>" /></span>
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
