package in.co.itlabs.minierp.services;

import java.util.List;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.sql2o.Connection;
import org.sql2o.Sql2o;

import in.co.itlabs.minierp.entities.Media;

@ApplicationScoped
public class MediaService {

	@Inject
	private DatabaseService databaseService;

	// create
	public int createMedia(List<String> messages, Media media) {
		int id = 0;
		Sql2o sql2o = databaseService.getSql2o();
		String sql = "insert into media (studentId, fileName, fileMime, fileBytes, remark, createdAt)"
				+ " values(:studentId, :fileName, :fileMime, :fileBytes, :remark, :createdAt)";

		try (Connection con = sql2o.open()) {
			id = con.createQuery(sql).addParameter("studentId", media.getStudentId())
					.addParameter("fileName", media.getFileName()).addParameter("fileMime", media.getFileMime())
					.addParameter("fileBytes", media.getFileBytes()).addParameter("remark", media.getRemark())
					.addParameter("createdAt", media.getCreatedAt()).executeUpdate().getKey(Integer.class);

			con.close();
		} catch (Exception e) {
			messages.add(e.getMessage());
		}
		return id;
	}

	public List<Media> getAllMedias(int studentId) {
		List<Media> medias = null;

		Sql2o sql2o = databaseService.getSql2o();
		String sql = "select * from media where studentId = :studentId order by createdAt desc";

		try (Connection con = sql2o.open()) {
			medias = con.createQuery(sql).addParameter("studentId", studentId).executeAndFetch(Media.class);
			con.close();
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
		return medias;
	}

	public List<Media> getImageMedias(int studentId) {
		List<Media> medias = null;

		Sql2o sql2o = databaseService.getSql2o();
		String sql = "select * from media where studentId = :studentId and lower(fileMime) like :fileMime order by createdAt desc";

		String fileMime = "%image%";
		try (Connection con = sql2o.open()) {
			medias = con.createQuery(sql).addParameter("studentId", studentId).addParameter("fileMime", fileMime)
					.executeAndFetch(Media.class);
			con.close();
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
		return medias;
	}

	public Media getMedia(int id) {
		Media media = null;

		Sql2o sql2o = databaseService.getSql2o();
		String sql = "select * from media where id = :id";

		try (Connection con = sql2o.open()) {
			media = con.createQuery(sql).addParameter("id", id).executeAndFetchFirst(Media.class);
			con.close();
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
		return media;
	}

	// update
	public boolean updateMedia(List<String> messages, Media media) {
		boolean success = false;
		Sql2o sql2o = databaseService.getSql2o();
		String sql = "update media set fileName = :fileName, fileMime = :fileMime, fileBytes = :fileBytes, remark = :remark"
				+ " where id = :id";

		try (Connection con = sql2o.open()) {
			con.createQuery(sql).addParameter("id", media.getId()).addParameter("fileName", media.getFileName())
					.addParameter("fileMime", media.getFileMime()).addParameter("fileBytes", media.getFileBytes())
					.addParameter("remark", media.getRemark()).executeUpdate();

			success = true;
			con.close();
		} catch (Exception e) {
			messages.add(e.getMessage());
		}
		return success;
	}

}