var gulp = require('gulp');
var livereload = require('gulp-livereload');

const FILES_TO_WATCH = ['app/Controller/**/*.php', 'app/Model/**/*.php', 'app/View/**/*.ctp',
    'app/webroot/css/**/*.css', 'app/webroot/js/**/*.js'];

gulp.task('php', function() {
    gulp.src(FILES_TO_WATCH)
        .pipe(livereload());
});

gulp.task('watch', function() {
    livereload.listen();
    gulp.watch(FILES_TO_WATCH, ['php']);
});
