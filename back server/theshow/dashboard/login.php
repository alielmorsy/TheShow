<?php
session_start();
include '../includes.php';
$display = "none";
if (isset($_SESSION['user_id'])) {
    header("Location: index.php");
} else
    if (isset($_POST['login'])) {
        $username = mysqli_real_escape_string($connectionU, $_POST['name']);
        $password = sha1(mysqli_real_escape_string($connectionU, $_POST['password']));
        $sql = "select * from admins where `userName`='$username'";
        $query = mysqli_query($connectionU, $sql);
        if ($query) {
            $row = mysqli_fetch_row($query);
            if ($query->num_rows == 0) {
                $display = "block";
            } else {
                $Password = $row[2];
                if ($password != $Password) {
                    $display = "block";
                } else {
                    $_SESSION['user_id'] = $username;
                    header("Location: index.php");
                }
            }
        }
    }
?>

<!DOCTYPE html>
<html lang="en">

<head>

    <meta charset="utf-8">
    <meta http-equiv="X-UA-Compatible" content="IE=edge">
    <meta name="viewport" content="width=device-width, initial-scale=1, shrink-to-fit=no">
    <meta name="description" content="">
    <meta name="author" content="The Show">

    <title>TheShow - Login</title>
    <link href="vendor/fontawesome-free/css/all.min.css" rel="stylesheet" type="text/css">
    <link href="https://fonts.googleapis.com/css?family=Nunito:200,200i,300,300i,400,400i,600,600i,700,700i,800,800i,900,900i"
          rel="stylesheet">

    <!-- Custom styles for this template-->
    <link href="css/sb-admin-2.min.css" rel="stylesheet">

</head>

<body class="bg-gradient-primary">

<div class="container">

    <!-- Outer Row -->
    <div class="row justify-content-center">

        <div class="col-5  col-xl-8">

            <div class="card o-hidden border-0 shadow-lg my-4">
                <div class="card-body p-0">
                    <!-- Nested Row within Card Body -->
                    <div class="row">
                        <div class="col-lg-6">
                            <div class="p-5">
                                <div class="text-center">
                                    <h1 class="h4 text-gray-900 mb-4">Login For Continue!</h1>
                                </div>
                                <form class="user" method="post" name="login">
                                    <div class="form-group">
                                        <input type="text" class="form-control form-control-user"
                                               id="name" aria-describedby="emailHelp"
                                               placeholder=" Enter UserName ..." name="name" required>
                                    </div>
                                    <div class="form-group">
                                        <input type="password" class="form-control form-control-user"
                                               id="exampleInputPassword" placeholder="Password" name="password"
                                               required>
                                    </div>
                                    <div class="form-group">
                                        <div class="custom-control custom-checkbox small">
                                            <input type="checkbox" class="custom-control-input" id="customCheck">
                                            <label class="custom-control-label" for="customCheck">Remember Me</label>
                                        </div>
                                    </div>
                                    <div class="form-group">
                                        <input type="submit" class="btn btn-primary btn-user btn-block" name="login">
                                    </div>
                                    <hr>
                                </form>
                                <div class="alert alert-warning align-content-lg-center" id="message" role="alert"
                                     style="display: <?php echo $display ?>">
                                    User Name or password error
                                </div>
                            </div>
                        </div>
                    </div>
                </div>
            </div>

        </div>

    </div>

</div>

<script src="vendor/jquery/jquery.js"></script>

<script src="vendor/bootstrap/js/bootstrap.bundle.js"></script>
<script src="vendor/jquery-easing/jquery.easing.min.js"></script>
<script src="js/sb-admin-2.min.js"></script>


</body>

</html>
