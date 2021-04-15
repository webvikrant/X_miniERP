package in.co.itlabs.minierp.business.services;

import java.sql.SQLException;
import java.time.LocalDate;
import java.time.ZoneOffset;

import org.sql2o.Sql2o;
import org.sql2o.converters.Converter;
import org.sql2o.converters.ConverterException;
import org.sql2o.quirks.NoQuirks;
import org.sql2o.quirks.Quirks;

import com.mysql.cj.jdbc.MysqlConnectionPoolDataSource;
import com.mysql.cj.jdbc.MysqlDataSource;
import com.vaadin.cdi.annotation.VaadinServiceScoped;

@VaadinServiceScoped
public class DatabaseService {
	private final Sql2o sql2o;

	public DatabaseService() throws SQLException {

		String mysqlUrl = "jdbc:mysql://localhost:3306/minierp";
		String mysqlUser = "root";
		String mysqlPassword = "";

//		sql2o = new Sql2o(url, user, password);
		final MysqlDataSource dataSource = new MysqlConnectionPoolDataSource();
		dataSource.setUrl(mysqlUrl);
		dataSource.setUser(mysqlUser);
		dataSource.setPassword(mysqlPassword);

		final Quirks quirks = new NoQuirks() {
			{
				converters.put(LocalDate.class, new LocalDateConverter());
			}
		};

		sql2o = new Sql2o(dataSource, quirks);
	}

	public Sql2o getSql2o() {
		return sql2o;
	}

	private class LocalDateConverter implements Converter<LocalDate> {
		@Override
		public LocalDate convert(final Object val) throws ConverterException {
			if (val instanceof java.sql.Date) {
				return ((java.sql.Date) val).toLocalDate();
			} else {
				return null;
			}
		}

		@Override
		public Object toDatabaseParam(final LocalDate val) {
			if (val == null) {
				return null;
			} else {
				return new java.sql.Date(val.atStartOfDay().toInstant(ZoneOffset.UTC).toEpochMilli());
			}
		}
	}
}
