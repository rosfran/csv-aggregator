package com.mendix.csv.aggregator;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static org.junit.Assert.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@AutoConfigureMockMvc
class AggregatorApplicationTests
{

	@Autowired
	private WebApplicationContext webApplicationContext;

	@Autowired private MockMvc mockMvc;

	@Autowired private ObjectMapper mapper;

	@Test
	@DisplayName("Requests the list of all medium files")
	void allMediumFilesRequested() throws Exception {
		String s = mockMvc
				.perform(get("/api/v1/medium-files"))
				.andExpect(status().is2xxSuccessful())
				.andReturn()
				.getResponse()
				.getContentAsString();


	}


	@Test
	@DisplayName("Requests the list of all small files")
	void allSmallFilesRequeste() throws Exception {
		mockMvc
				.perform(get("/api/v1/small-files"))
				.andExpect(status().is2xxSuccessful());
	}

	@Test
	@DisplayName("Requests that all medium CSV files be aggregated in sorted order")
	void aggregateCSVMediumFiles() throws Exception {
		mockMvc
				.perform(put("/api/v1/aggregated-csv?type=medium"))
				.andExpect(status().is2xxSuccessful())
				.andReturn()
				.getResponse();


		//System.out.println(s);
	}

	@Test
	@DisplayName("Requests that all small CSV files be aggregated in sorted order")
	void aggregateCSVSmallFiles() throws Exception {
		mockMvc
				.perform(put("/api/v1/aggregated-csv").
						param("type", "small"))
				.andExpect(status().is2xxSuccessful())
				.andReturn()
				.getResponse();

		//assertThat(results);
		//System.out.println(s);
	}

	@Test
	@DisplayName("Request sending small files to REST backend")
	public void testSendingFiles() throws Exception
	{

		final String f01Content = String.join("\n", new String[] { "actuators", "logjam",
				"unrepresentative", "trouper" } );

		final String f02Content = String.join("\n", new String[] { "poach", "tweaked",
				"madurai", "engineer" } );

		final String f03Content = String.join("\n", new String[] { "breakthrough", "grafting",
				"lowered", "soothingly", "sorcerers" } );

		MockMultipartFile firstFile = new MockMultipartFile(
				"csvData01",
				"f01.dat",
				"text/plain",
				f01Content.getBytes());
		MockMultipartFile secondFile = new MockMultipartFile(
				"csvData02",
				"f02.dat",
				"text/plain",
				f02Content.getBytes());
		MockMultipartFile thirdFile = new MockMultipartFile(
				"csvData02",
				"f02.dat",
				"text/plain",
				f03Content.getBytes());

		MockMvc mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
		mockMvc.perform(MockMvcRequestBuilders.multipart("/api/v1/files/")
						.file(firstFile)
						.file(secondFile)
						.file(thirdFile))
				.andExpect(status().is(HttpStatus.CREATED.value()));
	}



}
