package com.liverowing.liverowing.activity.workouttype


import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.liverowing.liverowing.R
import com.liverowing.liverowing.model.parse.WorkoutType
import com.liverowing.liverowing.loadUrl
import com.liverowing.liverowing.util.PicassoCircleTransformation
import kotlinx.android.synthetic.main.fragment_workout_type_details.*

private const val ARGUMENT_WORKOUT_TYPE = "workout_type"

class WorkoutTypeDetailsFragment : Fragment() {
    private lateinit var workoutType: WorkoutType

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_workout_type_details, container, false)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        workoutType = arguments.getParcelable(ARGUMENT_WORKOUT_TYPE)
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        f_workout_type_details_description.text = workoutType.descriptionText.toString()
        f_workout_type_details_createdby.text = workoutType.createdBy!!.username
        if (workoutType.createdBy!!.image != null && workoutType.createdBy!!.image?.url != null) {
            f_workout_type_details_createdby_image.loadUrl(workoutType.createdBy!!.image!!.url, PicassoCircleTransformation())
        }
    }

    companion object {
        fun newInstance(workoutType: WorkoutType): WorkoutTypeDetailsFragment {
            return WorkoutTypeDetailsFragment().apply {
                arguments = Bundle().apply {
                    putParcelable(ARGUMENT_WORKOUT_TYPE, workoutType)
                }
            }
        }
    }
}
