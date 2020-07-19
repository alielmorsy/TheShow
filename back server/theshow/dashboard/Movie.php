<?php
include '../includes.php';
$categories = getCategories($connectionU);
$languages = getLanguages($connectionU);
function getCategories($conn)
{
    $arr = array();
    $sql = "SELECT `category` FROM `categories` order by `category`  ";
    $query = mysqli_query($conn, $sql);
    if (!$query) die("Error In Database");
    while ($row = mysqli_fetch_row($query)) {
        $arr[] = $row[0];
    }

    return $arr;
}

function getLanguages($conn)
{
    $arr = array();
    $sql = "select language  from languages order by language ";
    $query = mysqli_query($conn, $sql);
    if (!$query) die("Error In Database");
    while ($row = mysqli_fetch_row($query)) {
        $arr[] = $row[0];
    }

    return $arr;

}

?>


<div class="page">

    <!-- Page Heading -->
    <div class="d-sm-flex align-items-center justify-content-between mb-4">
        <h1 class="h3 mb-0 text-gray-800">Add New Movie</h1>
    </div>
    <form>
        <div class="form-group">
            <label for="name">Movie Name</label>
            <input type="text" class="form-control" id="name" placeholder="Enter Movie Name">
        </div>
        <div class="form-group">
            <label for="image">Example file input</label>
            <input type="file" class="form-control-file" id="image">
        </div>

        <div class="form-group">
            <label for="duration">Duration</label>
            <input type="text" class="form-control" id="duration" placeholder="hh:mm:ss or mm:ss">
        </div>
        <div class="form-group">
            <label for="categories">Movies Categories</label>
            <select multiple class="selectpicker form-control" id="categories" data-container="body"
                    data-live-search="true" title="Select types of Categories" data-hide-disabled="true"
                    data-actions-box="true" data-virtual-scroll="false">
                <?php
                foreach ($categories as $cat) {
                    echo "<option value='$cat'>$cat</option>";
                }
                ?>
            </select>
        </div>
        <div class="form-group">
            <label for="tokenID">Token ID</label>
            <input type="text" class="form-control" id="tokenID">
        </div>
        <div class="form-group">
            <label for="language">Language</label>
            <select class="form-control" id="language">
                <?php
                foreach ($languages as $cat) {
                    echo "<option value='$cat'>$cat</option>";
                }
                ?>

            </select>
        </div>
        <div class="form-group has-search">
            <label for="form-control">Rating</label>
            <span class="fa fa-star form-control-feedback" style="color: #ffa500"></span>
            <input type="text" class="form-control" placeholder="Rating" id="rating">
        </div>
        <div class="form-group">
            <label for="description">Description</label>
            <textarea class="form-control" id="description" rows="3"></textarea>
        </div>

        <div class="form-group">
            <label for="year"> Year</label>
            <input type="text" class="form-control" placeholder="Year" id="year">
        </div>
        <div class="form-group">
            <button type="button" id="submit" class="btn btn-primary my-2 align-self-md-center"
                    value="Submit">
                <span>Submit</span>
            </button>
        </div>

    </form>
    <script type="text/javascript">
        const movie = new Movie();
        $(document).ready(function () {
            $('#categories').selectpicker();

            $('#cast-list').selectpicker();
            $('#image').change(function () {
                let value = document.getElementById("image").files[0];
                uploadImage(value, movie);
            });
            $('#submit').click(function () {
                const html = '   <span class="spinner-border spinner-border-sm" role="status" aria-hidden="true"></span>\n' +
                    '  Loading...';
                $('#submit').prop('disabled', true);
                document.getElementById("submit").innerHTML = html;
                collectMovieData();
            });
        });

        function collectMovieData() {
            const data = {};
            data['imageURL'] = movie.imageName;
            data['name'] = document.getElementById("name").value;

            data['duration'] = calcDuration(document.getElementById("duration").value);
            data['year'] = document.getElementById("year").value;
            data['types'] = handleCategories();
            data['rating'] = document.getElementById("rating").value;
            data['tokenID'] = document.getElementById("tokenID").value;
            data['description'] = document.getElementById("description").value;
            data['language'] = document.getElementById("language").value;
            data['type'] = "movie";
            data['what'] = "insert";
            sendData(data);
        }
    </script>
