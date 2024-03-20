




how to patch the database image with

https://github.com/gdraheim/docker-copyedit

needs linux/wsl to run, needs working docker wsl integration,
needs sudo if script is in /mnt/c/...

sudo ./docker-copyedit.py FROM mariadb INTO mariadb_dc_no_volume REMOVE ALL VOLUMES



how to commit database changes

start the patched image
run sql scripts
commit the changes with "docker commit"

docker commit id_of_running_container_with_the_data new_name_for_image_that_always_have_that_data

run this sql to make atomikos work on mysql/mariadb, this should happen in the image, but we mustn't forget that it is necessary.


für mysql essentiell
GRANT XA_RECOVER_ADMIN ON *.* TO connector;
FLUSH PRIVILEGES;

?pinGlobalTxToPhysicalConnection=true

bei mariadb reicht das an die url anzuhängen
?pinGlobalTxToPhysicalConnection=true


length = 21844) // max length for varchar using 3-byte utf8 chars