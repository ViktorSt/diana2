package se.lth.immun

import java.io._
import java.util.zip._


object ReportStreamer {
	
	
	def ensureDir(dir:File) = {
		if (!dir.exists)
			dir.mkdir
	}
	
	def apply(params:DianaParams, outQcZipFile:File):ReportStreamer = {
		if (params.zipQcFolder) 
			new ZipReportStreamer(outQcZipFile)
		else {
			if (params.nReport > 0)
				ensureDir(new File("qc"))
						
			new FileReportStreamer(new File("qc"))
		}
	}
}

trait ReportStreamer {
	def streamByPath(relPath:String):OutputStream
	def closeLast:Unit
	def close:Unit
}

class FileReportStreamer(baseDir:File) extends ReportStreamer {
	var curr:Option[BufferedOutputStream] = None
	def streamByPath(relPath:String):OutputStream = {
		curr = Some(new BufferedOutputStream(new FileOutputStream(new File(baseDir, relPath))))
		curr.get
	}
	def closeLast:Unit = {
		curr.foreach(s => s.close)
		curr = None
	}
	def close:Unit = {}
} 

class ZipReportStreamer(zipFile:File) extends ReportStreamer {
	val zos = new ZipOutputStream(new BufferedOutputStream(new FileOutputStream(zipFile)))
	val base = optDropExt(".zip")(zipFile.getName)
	var curr:Option[ZipEntry] = None
	
	def streamByPath(relPath:String):OutputStream = {
		curr = Some(new ZipEntry(base+"/"+relPath))
		zos.putNextEntry(curr.get)
        zos
	}
	
	def closeLast:Unit = 
		if (curr.isDefined) {
			zos.closeEntry
			curr = None
		}
		
	def optDropExt(ext:String)(name:String) =
		if (name.toLowerCase.endsWith(ext))
			name.dropRight(ext.length)
		else name
		
	def close = zos.close
}