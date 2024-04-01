package eu.domibus.connector.ui.persistence.service.impl;

import eu.domibus.connector.ui.dto.WebReportEntry;
import eu.domibus.connector.ui.persistence.dao.DomibusConnectorWebReportDao;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.support.JdbcDaoSupport;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;


@Repository
public class DomibusConnectorWebReportDaoImpl extends JdbcDaoSupport implements DomibusConnectorWebReportDao, InitializingBean {
    private static final SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss");
    protected final Log logger = LogFactory.getLog(getClass());
    private String reportIncludingEvidencesSQL;
    private String reportExcludingEvidencesSQL;

    @Autowired
    public DomibusConnectorWebReportDaoImpl(DataSource ds) {
        this.setDataSource(ds);
    }

    @Override
    public List<WebReportEntry> loadReportWithEvidences(Date fromDate, Date toDate) {
        if (this.reportIncludingEvidencesSQL == null) {
            loadQueries();
        }

        toDate = convertToDate(toDate);

        Date[] parameter = new Date[4];
        parameter[0] = fromDate;
        parameter[1] = toDate;
        parameter[2] = fromDate;
        parameter[3] = toDate;

        return getJdbcTemplate()
                .query(
                        this.reportIncludingEvidencesSQL,
                        parameter,
                        new BeanPropertyRowMapper<>(WebReportEntry.class)
                );
    }

    @Override
    public List<WebReportEntry> loadReport(Date fromDate, Date toDate) {
        if (this.reportExcludingEvidencesSQL == null) {
            loadQueries();
        }

        toDate = convertToDate(toDate);

        Date[] parameter = new Date[4];
        parameter[0] = fromDate;
        parameter[1] = toDate;
        parameter[2] = fromDate;
        parameter[3] = toDate;

        return getJdbcTemplate().query(
                this.reportExcludingEvidencesSQL,
                parameter,
                new BeanPropertyRowMapper<>(WebReportEntry.class)
        );
    }

    private Date convertToDate(Date toDate) {
        String dateString = sdf.format(toDate);
        String newDateString = dateString.substring(0, dateString.indexOf(" ") + 1) + "23:59:59";
        Date newToDate = null;
        try {
            newToDate = sdf.parse(newDateString);
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
        return newToDate;
    }

    private void loadQueries() {
        String query1 = loadQueryFile("/report_queries/report_excl_evidences.sql");

        String query2 = loadQueryFile("/report_queries/report_incl_evidences.sql");

        this.reportExcludingEvidencesSQL = query1;
        this.reportIncludingEvidencesSQL = query2;
    }

    private String loadQueryFile(String pathToResource) {
        StringBuffer sb = new StringBuffer();
        try {
            Resource resource = new ClassPathResource(pathToResource);
            DataInputStream in = new DataInputStream(resource.getInputStream());
            BufferedReader br = new BufferedReader(new InputStreamReader(in));
            String strLine;
            while ((strLine = br.readLine()) != null) {
                sb.append(" " + strLine);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return sb.toString();
    }
}
