package blibli.com.db2csv.configuration;

import blibli.com.db2csv.Customer;
import blibli.com.db2csv.CustomerRowMapper;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.item.database.JdbcPagingItemReader;
import org.springframework.batch.item.database.Order;
import org.springframework.batch.item.database.support.PostgresPagingQueryProvider;
import org.springframework.batch.item.file.FlatFileItemWriter;
import org.springframework.batch.item.file.transform.PassThroughLineAggregator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.FileSystemResource;

import javax.sql.DataSource;
import java.io.File;
import java.util.HashMap;
import java.util.Map;

@Configuration
public class JobConfiguration {

    @Autowired
    public JobBuilderFactory jobBuilderFactory;

    @Autowired
    public StepBuilderFactory stepBuilderFactory;

    @Autowired
    public DataSource dataSource;

    @Bean
    public JdbcPagingItemReader<Customer> reader(){
        JdbcPagingItemReader<Customer> reader = new JdbcPagingItemReader<>();

        reader.setDataSource(this.dataSource);
        reader.setFetchSize(10);
        reader.setRowMapper(new CustomerRowMapper());

        PostgresPagingQueryProvider provider = new PostgresPagingQueryProvider();
        provider.setSelectClause("id, firstName, lastName, birthdate");
        provider.setFromClause("from customer");

        Map<String,Order> sortkey = new HashMap<>(1);
        sortkey.put("id",Order.ASCENDING);
        provider.setSortKeys(sortkey);
        reader.setQueryProvider(provider);
        return reader;
    }

    @Bean
    public FlatFileItemWriter<Customer> writer() throws Exception{
        FlatFileItemWriter<Customer> writer = new FlatFileItemWriter<>();
        writer.setLineAggregator(new PassThroughLineAggregator<>()); //will run toString
        String outputPath = File.createTempFile("customerOutput", ".csv").getAbsolutePath();
        System.out.println("HEY! "+outputPath);
        writer.setResource(new FileSystemResource(outputPath));
        writer.afterPropertiesSet();
        return writer;
    }

    @Bean
    public Step mysteps11() throws Exception{
        return stepBuilderFactory.get("mysteps11")
                .<Customer,Customer>chunk(10)
                .reader(reader())
                .writer(writer())
                .build();
    }

    @Bean
    public Job myJob() throws Exception{
        return jobBuilderFactory.get("myJob")
                .start(mysteps11())
                .build();
    }
}
