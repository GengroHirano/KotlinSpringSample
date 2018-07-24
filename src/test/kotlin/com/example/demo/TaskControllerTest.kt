package com.example.demo

import com.example.todolist.JdbcTaskRepository
import com.example.todolist.Task
import com.example.todolist.TaskRepository
import org.hamcrest.Matchers
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.CommandLineRunner
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.test.context.junit4.SpringRunner
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.*
import java.util.regex.Matcher

@RunWith(SpringRunner::class)
@WebMvcTest(TaskRepository::class)
class TaskControllerTest {

    @Autowired
    private lateinit var mockMvc: MockMvc

    @MockBean
    private lateinit var taskRepository: JdbcTaskRepository

    @MockBean
    private lateinit var commandLineRunner: CommandLineRunner

    @Test
    fun index_保存されているタスクが全件表示されること() {

        val tasks = listOf(
                Task(id = 1, content = "TASK1", done = false),
                Task(id = 2, content = "TASK2", done = true)
        )

        Mockito.`when`(taskRepository.findAll()).thenReturn(tasks)

        mockMvc.perform(MockMvcRequestBuilders.get("/tasks"))
                .andExpect(view().name("tasks/index"))
                .andExpect(model().attribute("tasks", tasks))
                .andExpect(content().string(Matchers.containsString("<span>TASK1</span>")))
                .andExpect(content().string(Matchers.containsString("<s>TASK2</s>")))

    }

    @Test
    fun create_ポストされている内容をもとにTaskを新規作成すること() {
        mockMvc.perform(MockMvcRequestBuilders.post("/tasks")
                .param("content", "TASK3"))
                .andExpect(redirectedUrl("/tasks"))

        Mockito.verify(taskRepository).create("TASK3")
    }
}
