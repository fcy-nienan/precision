<!DOCTYPE html>
<html>
<head>
    <script src="https://unpkg.com/vue@2.1.10/dist/vue.js"></script>
    <script src="https://unpkg.com/vue-select@2.0.0/dist/vue-select.js"></script>
    <link href="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.6/css/bootstrap.min.css" rel="stylesheet" />
    <meta charset="utf-8">
    <meta name="viewport" content="width=device-width">
    <title>JS Bin</title>
</head>
<body>
<div id="app" class="container-fluid">
    <h2>VueSelect Example</h2>
    <v-select :placeholder="placeholder"
              multiple
              :options="options"
              label="name"
              @input="selecte_student"
              :value.sync="student_obj"></v-select>
    <div id="value">{{students}}</div>
</div>
<script>
    Vue.component('v-select', VueSelect.VueSelect);

    new Vue({
        el: '#app',
        data: function() {
            return {
                options: [
                    {"student_id": 1, "name": "Vence"},
                    {"student_id": 2, "name": "Job"},
                    {"student_id": 3, "name": "Jack"},
                ],
                placeholder: 'Choose a student..',
                students: [1],
                student_obj: []
            }
        },
        mounted: function() {
            var student_filter = function (obj) {
                return this.students.indexOf(obj.student_id) > -1
            }
            this.student_obj = this.options.filter(student_filter, this)
        },
        methods:{
            selecte_student: function(values){
                this.students =values.map(function(obj){
                    return obj.student_id
                })
            }
        }
    });
</script>
</body>
</html>