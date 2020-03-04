<html>
<header>
    <meta charset="UTF-8"/>
    <script src="iview/vue.min.js"></script>
    <!-- 引入样式 -->
    <link rel="stylesheet" href="iview/iview.css">
    <!-- 引入组件库 -->
    <script src="iview/iview.min.js"></script>
</header>
<body>
<div  id="app">
    <i-form v-ref:form-validate :model="formValidate" :rules="ruleValidate" :label-width="80">
        <Form-item label="姓名" prop="name">
            <i-input :value.sync="formValidate.name" placeholder="请输入姓名"></i-input>
        </Form-item>
        <Form-item label="邮箱" prop="mail">
            <i-input :value.sync="formValidate.mail" placeholder="请输入邮箱"></i-input>
        </Form-item>
        <Form-item label="城市" prop="city">
            <i-select :model.sync="formValidate.city" placeholder="请选择所在地">
                <i-option value="beijing">北京市</i-option>
                <i-option value="shanghai">上海市</i-option>
                <i-option value="shenzhen">深圳市</i-option>
            </i-select>
        </Form-item>
        <Form-item label="选择日期">
            <Row>
                <i-col span="11">
                    <Form-item prop="date">
                        <Date-picker type="date" placeholder="选择日期" :value.sync="formValidate.date"></Date-picker>
                    </Form-item>
                </i-col>
                <i-col span="2" style="text-align: center">-</i-col>
                <i-col span="11">
                    <Form-item prop="time">
                        <Time-picker type="time" placeholder="选择时间" :value.sync="formValidate.time"></Time-picker>
                    </Form-item>
                </i-col>
            </Row>
        </Form-item>
        <Form-item label="性别" prop="gender">
            <Radio-group :model.sync="formValidate.gender">
                <Radio value="male">男</Radio>
                <Radio value="female">女</Radio>
            </Radio-group>
        </Form-item>
        <Form-item label="爱好" prop="interest">
            <Checkbox-group :model.sync="formValidate.interest">
                <Checkbox value="吃饭"></Checkbox>
                <Checkbox value="睡觉"></Checkbox>
                <Checkbox value="跑步"></Checkbox>
                <Checkbox value="看电影"></Checkbox>
            </Checkbox-group>
        </Form-item>
        <Form-item label="介绍" prop="desc">
            <i-input :value.sync="formValidate.desc" type="textarea" :autosize="{minRows: 2,maxRows: 5}" placeholder="请输入..."></i-input>
        </Form-item>
        <Form-item>
            <i-button type="primary" @click="handleSubmit('formValidate')">提交</i-button>
            <i-button type="ghost" @click="handleReset('formValidate')" style="margin-left: 8px">重置</i-button>
        </Form-item>
    </i-form>
</div>
<script>

    new Vue({
        el: '#app',
        data: {
            formValidate: {
                name: '',
                mail: '',
                city: '',
                gender: '',
                interest: [],
                date: '',
                time: '',
                desc: ''
            },
            ruleValidate: {
                name: [
                    { required: true, message: 'The name cannot be empty', trigger: 'blur' }
                ],
                mail: [
                    { required: true, message: 'Mailbox cannot be empty', trigger: 'blur' },
                    { type: 'email', message: 'Incorrect email format', trigger: 'blur' }
                ],
                city: [
                    { required: true, message: 'Please select the city', trigger: 'change' }
                ],
                gender: [
                    { required: true, message: 'Please select gender', trigger: 'change' }
                ],
                interest: [
                    { required: true, type: 'array', min: 1, message: 'Choose at least one hobby', trigger: 'change' },
                    { type: 'array', max: 2, message: 'Choose two hobbies at best', trigger: 'change' }
                ],
                date: [
                    { required: true, type: 'date', message: 'Please select the date', trigger: 'change' }
                ],
                time: [
                    { required: true, type: 'string', message: 'Please select time', trigger: 'change' }
                ],
                desc: [
                    { required: true, message: 'Please enter a personal introduction', trigger: 'blur' },
                    { type: 'string', min: 20, message: 'Introduce no less than 20 words', trigger: 'blur' }
                ]
            }
        },
        methods: {
            handleSubmit (name) {
                this.$refs[name].validate((valid) => {
                    if (valid) {
                        this.$Message.success('Success!');
                    } else {
                        this.$Message.error('Fail!');
                    }
                })
            },
            handleReset (name) {
                this.$refs[name].resetFields();
            }
        }
    })

</script>

</body>
</html>