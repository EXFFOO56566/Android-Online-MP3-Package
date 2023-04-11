<?php include("includes/header.php");

	require("includes/function.php");
	require("language/language.php");

 
	$cat_qry="SELECT * FROM tbl_category ORDER BY category_name";
	$cat_result=mysqli_query($mysqli,$cat_qry); 

  $album_qry="SELECT * FROM tbl_album ORDER BY album_name";
  $album_result=mysqli_query($mysqli,$album_qry); 

  $art_qry="SELECT * FROM tbl_artist ORDER BY artist_name";
  $art_result=mysqli_query($mysqli,$art_qry); 
	
	if(isset($_POST['submit']))
	{
 			
				
        if ($_POST['mp3_type']=='server_url')
        {
              $mp3_url=$_POST['mp3_url'];

              $mp3_thumbnail=rand(0,99999)."_".$_FILES['mp3_thumbnail']['name'];
       
              //Main Image
              $tpath1='images/'.$mp3_thumbnail;        
              $pic1=compress_image($_FILES["mp3_thumbnail"]["tmp_name"], $tpath1, 80);
         
              //Thumb Image 
              $thumbpath='images/thumbs/'.$mp3_thumbnail;   
              $thumb_pic1=create_thumb_image($tpath1,$thumbpath,'200','200');   

 
        } 

        if ($_POST['mp3_type']=='local')
        {

              $file_path = 'http://'.$_SERVER['SERVER_NAME'] . dirname($_SERVER['REQUEST_URI']).'/uploads/';
              
              $mp3_url=$file_path.$_POST['mp3_file_name'];

              $mp3_thumbnail=rand(0,99999)."_".$_FILES['mp3_thumbnail']['name'];
       
              //Main Image
              $tpath1='images/'.$mp3_thumbnail;        
              $pic1=compress_image($_FILES["mp3_thumbnail"]["tmp_name"], $tpath1, 80);
         
              //Thumb Image 
              $thumbpath='images/thumbs/'.$mp3_thumbnail;   
              $thumb_pic1=create_thumb_image($tpath1,$thumbpath,'200','200');   

         } 


          
        $data = array( 
			    'cat_id'  =>  $_POST['cat_id'],
          'album_id'  =>  $_POST['album_id'],
 			    'mp3_title'  =>  $_POST['mp3_title'],
          'mp3_type'  =>  $_POST['mp3_type'],
          'mp3_url'  =>  $mp3_url,
          'mp3_thumbnail'  =>  $mp3_thumbnail,
          'mp3_duration'  =>  $_POST['mp3_duration'],
          'mp3_artist'  => implode(',', $_POST['mp3_artist']),
          'mp3_description'  =>  $_POST['mp3_description'],
			    );		

		 		$qry = Insert('tbl_mp3',$data);	

 	    
		$_SESSION['msg']="10";
 
		header( "Location:add_mp3.php");
		exit;	

		 
	}
	
	  
?>
<script src="http://ajax.googleapis.com/ajax/libs/jquery/1/jquery.min.js"></script>
<script>
            $(function () {
                $('#btn').click(function () {
                    $('.myprogress').css('width', '0');
                    $('.msg').text('');
                    var mp3_local = $('#mp3_local').val();
                    if (mp3_local == '') {
                        alert('Please enter file name and select file');
                        return;
                    }
                    var formData = new FormData();
                    formData.append('mp3_local', $('#mp3_local')[0].files[0]);
                    $('#btn').attr('disabled', 'disabled');
                     $('.msg').text('Uploading in progress...');
                    $.ajax({
                        url: 'uploadscript.php',
                        data: formData,
                        processData: false,
                        contentType: false,
                        type: 'POST',
                        // this part is progress bar
                        xhr: function () {
                            var xhr = new window.XMLHttpRequest();
                            xhr.upload.addEventListener("progress", function (evt) {
                                if (evt.lengthComputable) {
                                    var percentComplete = evt.loaded / evt.total;
                                    percentComplete = parseInt(percentComplete * 100);
                                    $('.myprogress').text(percentComplete + '%');
                                    $('.myprogress').css('width', percentComplete + '%');
                                }
                            }, false);
                            return xhr;
                        },
                        success: function (data) {
                         
                            $('#mp3_file_name').val(data);
                            $('.msg').text("File uploaded successfully!!");
                            $('#btn').removeAttr('disabled');
                        }
                    });
                });
            });
        </script>
<script type="text/javascript">
$(document).ready(function(e) {
           $("#mp3_type").change(function(){
          
           var type=$("#mp3_type").val();
              
              if(type=="server_url")
              {
                 
                 $("#mp3_url_display").show();
                 $("#thumbnail").show();
                 $("#mp3_local_display").hide();
              }
              else
              {   
                     
                $("#mp3_url_display").hide();               
                $("#mp3_local_display").show();
                $("#thumbnail").show();

              }    
              
         });
        });
</script>
<div class="row">
      <div class="col-md-12">
        <div class="card">
          <div class="page_title_block">
            <div class="col-md-5 col-xs-12">
              <div class="page_title">Add Mp3</div>
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
            <form action="" name="add_form" method="post" class="form form-horizontal" enctype="multipart/form-data">
 
              <div class="section">
                <div class="section-body">
                   <div class="form-group">
                    <label class="col-md-3 control-label">Title :-</label>
                    <div class="col-md-6">
                      <input type="text" name="mp3_title" id="mp3_title" value="" class="form-control" required>
                    </div>
                  </div>
                  <div class="form-group">
                    <label class="col-md-3 control-label">Duration :-</label>
                    <div class="col-md-6">
                      <input type="text" name="mp3_duration" id="mp3_duration" value="" class="form-control" required>
                    </div>
                  </div>
                   <div class="form-group">
                    <label class="col-md-3 control-label">Category :-</label>
                    <div class="col-md-6">
                      <select name="cat_id" id="cat_id" class="select2" required>
                        <option value="">--Select Category--</option>
          							<?php
          									while($cat_row=mysqli_fetch_array($cat_result))
          									{
          							?>          						 
          							<option value="<?php echo $cat_row['cid'];?>"><?php echo $cat_row['category_name'];?></option>	          							 
          							<?php
          								}
          							?>
                      </select>
                    </div>
                  </div>
                  <div class="form-group">
                    <label class="col-md-3 control-label">Album :-</label>
                    <div class="col-md-6">
                      <select name="album_id" id="album_id" class="select2">
                        <option value="">--Select Album--</option>
                        <?php
                            while($album_row=mysqli_fetch_array($album_result))
                            {
                        ?>                       
                        <option value="<?php echo $album_row['aid'];?>"><?php echo $album_row['album_name'];?></option>                           
                        <?php
                          }
                        ?>
                      </select>
                    </div>
                  </div>
                  <div class="form-group">
                    <label class="col-md-3 control-label">Artist :-</label>
                    <div class="col-md-6">
                      <select name="mp3_artist[]" id="mp3_artist" class="select2 form-control" required multiple="multiple">
                        <option value="">--Select Artist--</option>
                        <?php
                            while($art_row=mysqli_fetch_array($art_result))
                            {
                        ?>                       
                        <option value="<?php echo $art_row['artist_name'];?>"><?php echo $art_row['artist_name'];?></option>                           
                        <?php
                          }
                        ?>
                      </select>
                    </div>
                  </div>
                  <div class="form-group">
                    <label class="col-md-3 control-label">Upload From :-</label>
                    <div class="col-md-6">                       
                      <select name="mp3_type" id="mp3_type" style="width:280px; height:25px;" class="select2" required>
                            <option value="server_url">From Server(URL)</option>
                            <option value="local">Browse From Device</option>
                      </select>
                    </div>
                  </div>
                  <div id="mp3_url_display" class="form-group">
                    <label class="col-md-3 control-label">Song URL :-</label>
                    <div class="col-md-6">
                      <input type="text" name="mp3_url" id="mp3_url" value="" class="form-control">
                    </div>
                  </div>
                  <div id="mp3_local_display" class="form-group" style="display:none;">
                    <label class="col-md-3 control-label">Song Upload :-</label>
                    <div class="col-md-6">
                    
                    <input type="hidden" name="mp3_file_name" id="mp3_file_name" value="" class="form-control">
                      <input type="file" name="mp3_local" id="mp3_local" value="" class="form-control">

                      <div class="progress">
                            <div class="progress-bar progress-bar-success myprogress" role="progressbar" style="width:0%">0%</div>
                        </div>

                        <div class="msg"></div>
                        <input type="button" id="btn" class="btn-success" value="Upload" />
                    </div>
                  </div><br>
                  <div id="thumbnail" class="form-group">
                    <label class="col-md-3 control-label">Image:-
                      (Recommended resolution: 300x300,400x400 or Square Image)
                    </label>
                    <div class="col-md-6">
                      <div class="fileupload_block">
                        <input type="file" name="mp3_thumbnail" value="" id="fileupload">
                       <div class="fileupload_img"><img type="image" src="assets/images/add-image.png" alt="category image" /></div>
                      </div>
                    </div>
                  </div>
                  <div class="form-group">
                    <label class="col-md-3 control-label">Song Description :-</label>
                    <div class="col-md-6">                    
                      <textarea name="mp3_description" id="mp3_description" class="form-control"></textarea>

                      <script>CKEDITOR.replace( 'mp3_description' );</script>
                    </div>
                  </div><br>
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
