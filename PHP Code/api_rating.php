<?php include("includes/connection.php"); 
      include("includes/function.php");
  
    
    if(isset($_GET['post_id']))
  {
     
      //search if the user(ip) has already gave a note
      $ip = $_GET['device_id'];
      $post_id = $_GET['post_id'];
      $therate = $_GET['rate'];

  
      $query1 = mysqli_query($mysqli,"select * from tbl_rating where post_id  = '$post_id' && ip = '$ip' "); 
      while($data1 = mysqli_fetch_assoc($query1)){
        $rate_db1[] = $data1;
      }
      if(@count($rate_db1) == 0 ){
      
           $data = array(            
              'post_id'  =>$post_id,
              'rate'  =>  $therate,
               'ip'  => $ip,
               );  
  $qry = Insert('tbl_rating',$data); 
      
          //Total rate result
           
        $query = mysqli_query($mysqli,"select * from tbl_rating where post_id  = '$post_id' ");
               
         while($data = mysqli_fetch_assoc($query)){
                    $rate_db[] = $data;
                    $sum_rates[] = $data['rate'];
               
                }
        
                if(@count($rate_db)){
                    $rate_times = count($rate_db);
                    $sum_rates = array_sum($sum_rates);
                    $rate_value = $sum_rates/$rate_times;
                    $rate_bg = (($rate_value)/5)*100;
                }else{
                    $rate_times = 0;
                    $rate_value = 0;
                    $rate_bg = 0;
                }
         
        $rate_avg=round($rate_value); 
        
      $sql="update tbl_mp3 set total_rate=total_rate + 1,rate_avg='$rate_avg' where id='".$post_id."'";

      mysqli_query($mysqli,$sql);
        
      $total_rat_sql="SELECT * FROM tbl_mp3 WHERE id='".$post_id."'";
      $total_rat_res=mysqli_query($mysqli,$total_rat_sql);
      $total_rat_row=mysqli_fetch_assoc($total_rat_res);
    
        echo '{"EBOOK_APP":[{"total_rate":'.$total_rat_row['total_rate'].',"rate_avg":'.$total_rat_row['rate_avg'].',"MSG":"You have succesfully rated"}]}';
        
      }else{
                
        echo '{"EBOOK_APP":[{"MSG":"You have already rated"}]}';
      }
   
  }
  else
  {
    echo '{"EBOOK_APP":[{"MSG":"Error"}]}';
    
  }
?>