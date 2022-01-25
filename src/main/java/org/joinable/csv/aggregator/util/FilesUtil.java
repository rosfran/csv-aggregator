package org.joinable.csv.aggregator.util;

import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.FileAttribute;
import java.nio.file.attribute.PosixFilePermission;
import java.nio.file.attribute.PosixFilePermissions;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Stream;

public class FilesUtil
{

    static public boolean copyFilesToDir(MultipartFile[] multipartFiles, String dirVolume )
    {

        for (MultipartFile mFile : multipartFiles)
        {

            try
            {

                String fileName = mFile.getOriginalFilename();

                File file = new File(dirVolume + fileName);

                mFile.transferTo(file);

                //assertThat(FileUtils.readFileToString(new File("src/main/resources/targetFile.tmp"), "UTF-8"))
                //       .isEqualTo("Hello World");

            }
            catch (IOException e)
            {
                e.printStackTrace();

                return false;
            }
        }

        return true;
    }

    static public void deleteIfExists( String fileName )
    {

        Path caminho = Paths.get(fileName);

        try
        {
            Files.deleteIfExists(caminho);
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }

    }

    static public void copyToBaseDir( String dir, String to )
    {

        Path caminho = Paths.get(dir);

        Path dest = Paths.get(to);

        Path file = null;

        if ( Files.exists(caminho)  && Files.isDirectory(caminho))
        {
            try
            {
                Stream<Path> s = Files.find(caminho,
                        1,
                        (path, basicFileAttributes) -> path.toFile().getName().matches(".*.csv")
                );

                Optional<Path> p = s.findFirst();

                if ( p.isPresent())
                {
                    file = p.get();
                }

                Files.move(file, dest, StandardCopyOption.REPLACE_EXISTING );
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }
        }

    }

    public static Path createFile(String arqOut, String cabecalho)
    {
        Path caminho = Paths.get(arqOut);
        if (Files.notExists(caminho))
        {

            Set<PosixFilePermission> perms = PosixFilePermissions.fromString("rwxrwxrwx");
            FileAttribute attr = PosixFilePermissions.asFileAttribute(perms);
            try
            {
                Files.createFile(caminho, attr);
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }

            try
            {
                Files.write(caminho, (cabecalho + "\n").getBytes(),
                        StandardOpenOption.CREATE,
                        StandardOpenOption.TRUNCATE_EXISTING, StandardOpenOption.WRITE);
            }
            catch (IOException ioException)
            {
                ioException.printStackTrace();
            }
        }


        return caminho;
    }

}
