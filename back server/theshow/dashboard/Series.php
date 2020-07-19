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
        <h1 class="h3 mb-0 text-gray-800">Add New Series</h1>
    </div>
    <form class="needs-validation">

        <div class="form-group">
            <label for="name">Series Name</label>
            <input type="text" class="form-control" id="name" placeholder="Enter Series Name" required>
        </div>
        <div class="form-group">
            <div class="custom-file">
                <input type="file" class="custom-file-input" id="series-image" data-toggle="modal"
                >
                <label class="custom-file-label" for="series-image" id="seriesFileName">Choose image</label>
            </div>
        </div>
        <div class="form-group">
            <label for="categories">Series Categories</label>
            <select multiple class="selectpicker form-control" id="categories" data-container="body"
                    data-live-search="true" title="Select your Categories" data-hide-disabled="true"
                    data-actions-box="true" data-virtual-scroll="false" required>
                <?php
                foreach ($categories as $cat) {
                    echo "<option value='$cat'>$cat</option>";
                }
                ?>

            </select>
        </div>
        <div class="form-group">
            <label for="language">Language</label>
            <select class="form-control" id="language" required>
                <?php
                foreach ($languages as $cat) {
                    echo "<option value='$cat'>$cat</option>";
                }
                ?>
            </select>
        </div>
        <div class="form-group has-search">
            <label>Rating</label>
            <span class="fa fa-star form-control-feedback" style="color: orange"></span>
            <input type="text" class="form-control" placeholder="Rating" required id="rating">
        </div>
        <div class="form-group">
            <label for="description">Description</label>
            <textarea class="form-control" id="description" rows="3"></textarea>
        </div>

        <div class="form-group">
            <label for="release"> Release On</label>
            <input type="text" class="form-control" placeholder="Release On" required id="release">
        </div>

        <br>
        <br>
        <div id="newSeason" class="form-group-lg">

        </div>


        <div class="form-group">
            <a class="align-self-md-center h8" href="javascript:void(0);" id="add-newSeason">
                <li class="fa fa-plus"></li>
                <label>Add New Season</label>
            </a>
        </div>


        <div class="form-group">
            <button type="button" id="submit" class="btn btn-primary my-2 align-content-center"
                    value="Submit">
                <span>Submit</span>
            </button>
        </div>
    </form>

    <script src="js/SeasonConfiguration.js"></script>
</div>