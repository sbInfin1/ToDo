# ToDo
Android App to make Todo Lists and set Notifications for Tasks

## Nav Drawer, Task categories and Task List
<div class="row">
  <img src="https://github.com/sbInfin1/ToDo/blob/Using_workManager/screenshots/screenshot_categories.jpg" width="300" height="600" title="Nav Drawer showing various categories"/>
  <img src="https://github.com/sbInfin1/ToDo/blob/Using_workManager/screenshots/screenshots_mixed_tasks.jpg" width="300" height="600" title="All tasks"/>
</div>
The images above show the Nav Drawer, which by default comes with the <strong>Show all tasks</strong> and <strong>Manage categories</strong> options. The <strong>Manage categories</strong> option is used to create new categories to which the tasks will belong to. These custom added categories will be listed below the default options in the Nav Drawer. Upon selecting a category (by clicking on it), the tasks belonging only to that particular category will be visible in the list. 

## Manage Categories
<p align="center">
  <img src="https://github.com/sbInfin1/ToDo/blob/Using_workManager/screenshots/screenshot_add_new_category.jpg" width="300" height="600" title="Task created"/>
</p>
User need to add custom categories before adding any task to the list. New categories can be added by clicking on the <strong>Manage Categories</strong> menu option. This launches a Alert dialog EditText where the user can type the name of the new category.

## Creating New Task
<div class="row">
  <img src="https://github.com/sbInfin1/ToDo/blob/Using_workManager/screenshots/screenshot_adding_new_task.jpg" width="300" height="600" title="Adding new task"/>
  <img src="https://github.com/sbInfin1/ToDo/blob/Using_workManager/screenshots/screenshot_task_created.jpg" width="300" height="600" title="Task created"/>
</div>
In order to create a new task, the user need to click on the floating-action button in the bottom right corner of the task list screen. In the above screenshot, we can see a new task named "Drink Water" has been created with the <strong>due time</strong> of 5:44 pm.

## Deleting Tasks
<div class="row">
  <img src="https://github.com/sbInfin1/ToDo/blob/Using_workManager/screenshots/screenshot_deleting_by_swiping.jpg" width="300" height="600" title="Deleting by swiping"/>
  <img src="https://github.com/sbInfin1/ToDo/blob/Using_workManager/screenshots/screenshot_delete_confirmation_alert_dialog.jpg" width="300" height="600" title="Task delete confirmation dialog"/>
  <img src="https://github.com/sbInfin1/ToDo/blob/Using_workManager/screenshots/screenshot_task_not_deleted.jpg" width="300" height="600" title="Task not deleted"/>
</div>
If the user wants to delete a previously created task, he/she can just swipe left or right on a task. This will pop up the <strong>Task Delete Confirmation</strong> dialog box, where the user can either confirm to delete the swiped task, or cancel deletion if swiped on the task by mistake.

## Notification and Snoozing
<div class="row">
  <img src="https://github.com/sbInfin1/ToDo/blob/Using_workManager/screenshots/screenshot_notification_shown.jpg" width="300" height="600" title="Notification shown"/>
  <img src="https://github.com/sbInfin1/ToDo/blob/Using_workManager/screenshots/screenshot_notification_snoozed.jpg" width="300" height="600" title="Notification Snoozed"/>
</div>
Here, we see that our task labelled "Drink water" has fired its reminder in the form of a notification at its <strong>due time</strong> of 5:44 pm. There is an action button associated with the notification named <strong>Snooze</strong>, upon clicking which we snooze the same notification for another 2 minutes from the time at which it is clicked.


<!-- ![alt text](https://github.com/sbInfin1/ToDo/blob/Using_workManager/screenshots/screenshot_categories.jpg "Nav Drawer showing various categories") {width = 50%} -->
