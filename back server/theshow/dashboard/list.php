<?php
if (isset($_GET['list']))
    $type = $_GET['list'];
else die("<span class=\"h1 align-middle\"> Oops Internal Error </span>");
?>
<style>
    .cover{
      width:50%;
      height:50%;
    }
</style>
<div class="page">

    <div class="d-sm-flex align-items-center justify-content-between mb-4">
        <h1 class="h3 mb-0 text-gray-800"><?php echo ucfirst($type); ?> List</h1>
    </div>
    <div class="table-responsive">
        <table class="table table-bordered table-hover" id="dataTable" width="100%" cellspacing="0">
            <thead>
            <tr role="row">
                <th>id</th>
                <th>image</th>
                <th>name</th>
                <th>year</th>
                <th>rating</th>
                <th>language</th>

            </tr>
            </thead>

            <tbody>
            </tbody>
        </table>
    </div>

    <script>
        $(document).ready(function () {
            $("#dataTable").DataTable({
                "processing": true,
                "serverSide": true,
                retrieve: true,
                "columns": [
                    {"data": "id"},
                    {"data": "image"},
                    {"data": "name"},
                    {"data": "year"},
                    {"data": "rating"},
                    {"data": "language"}
                ],
                ajax: "parentList.php?list=<?php echo $type; ?>"

            });
        });
    </script>
</div>
