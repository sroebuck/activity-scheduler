// Include gulp
var gulp = require('gulp'); 

// Include Our Plugins
var jshint = require('gulp-jshint');
var sass = require('gulp-sass');
var concat = require('gulp-concat');
var uglify = require('gulp-uglify');
var rename = require('gulp-rename');
var server = require('gulp-server-livereload');
var react = require('gulp-react');
var to5 = require('gulp-6to5');
 
// Lint Task
gulp.task('lint', function() {
    return gulp.src('dist/*.jsx')
        .pipe(jshint())
        .pipe(jshint.reporter('default'));
});

// Compile Our Sass
gulp.task('sass', function() {
    return gulp.src('scss/*.scss')
        .pipe(sass())
        .pipe(gulp.dest('css'));
});

// Concatenate & Minify JS
gulp.task('scripts', function() {
    return gulp.src('js/*.js')
        .pipe(concat('all.js'))
        .pipe(gulp.dest('dist'))
        .pipe(rename('all.min.js'))
        .pipe(uglify())
        .pipe(gulp.dest('dist'));
});

// Watch Files For Changes
gulp.task('watch', function() {
    gulp.watch('js/*.js', ['lint', 'scripts']);
    gulp.watch('js/*.jsx', ['6to5']);
    gulp.watch('scss/*.scss', ['sass']);
});

// Server Task
gulp.task('webserver', function() {
  gulp.src('.')
    .pipe(server({
      livereload: true,
      directoryListing: true,
      open: true
    }));
});

// React Task
gulp.task('react', function () {
    return gulp.src('js/*.jsx')
        .pipe(react())
        .pipe(gulp.dest('dist'));
});

// 6to5 Task
gulp.task('6to5', function () {
    return gulp.src('js/*.js*')
        .pipe(to5())
        .pipe(gulp.dest('dist'));
});

// Default Task
gulp.task('default', ['6to5', 'lint', 'sass', 'scripts', 'watch']);
